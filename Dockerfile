FROM java:8-jre-alpine
MAINTAINER Keith Woelke <keith.woelke@gmail.com>

EXPOSE 80/tcp

RUN apk update && apk add tcpflow

RUN mkdir -p /wiremock/
WORKDIR /wiremock
COPY __files/ /wiremock/
COPY mappings/ /wiremock/
COPY wiremock-standalone-2.7.1.jar /wiremock/

ENTRYPOINT ["java", "-cp", "wiremock-extensions.jar:wiremock-standalone-2.7.1.jar", "com.github.tomakehurst.wiremock.standalone.WireMockServerRunner", "--verbose", "--port", "80"]

COPY wiremock-extensions.jar /wiremock/

