package com.github.keithwoelke.wiremock.extensions;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.google.common.collect.Maps;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Iso8601DateTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        String responseBody = response.getBodyAsString();

        Map<String, String> replacements = Maps.newHashMap();
        replacements.put("${pastDateTimeRange}", String.format("%s/%s", ZonedDateTime.now(ZoneId.of("UTC")).minus(2, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT), ZonedDateTime.now(ZoneId.of("UTC")).minus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT)));
        replacements.put("${futureDateTimeRange}", String.format("%s/%s", ZonedDateTime.now(ZoneId.of("UTC")).plus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT), ZonedDateTime.now(ZoneId.of("UTC")).plus(2, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT)));
        replacements.put("${currentDateTimeRange}", String.format("%s/%s", ZonedDateTime.now(ZoneId.of("UTC")).minus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT), ZonedDateTime.now(ZoneId.of("UTC")).plus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT)));
        replacements.put("${pastDateTime}", ZonedDateTime.now(ZoneId.of("UTC")).minus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT));
        replacements.put("${currentDateTime}", ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT));
        replacements.put("${futureDateTime}", ZonedDateTime.now(ZoneId.of("UTC")).plus(1, ChronoUnit.MONTHS).format(DateTimeFormatter.ISO_INSTANT));

        String regex = "(\\$\\{[^}]+\\})";

        StringBuffer stringBuffer = new StringBuffer();
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(responseBody);

        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            if (replacement != null)
                matcher.appendReplacement(stringBuffer, replacement);
        }
        matcher.appendTail(stringBuffer);
        responseBody = stringBuffer.toString();

        Collection<HttpHeader> httpHeaders = response.getHeaders().all();
        httpHeaders.add(new HttpHeader("wiremock-transformer", "iso8601-date-transformer"));

        return Response.Builder.
                like(response).but().
                body(responseBody).
                headers(new HttpHeaders(httpHeaders)).
                build();
    }

    public String getName() {
        return "iso8601-date-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return true;
    }
}