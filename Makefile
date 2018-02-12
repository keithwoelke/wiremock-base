prepare:
	mvn clean package
	cp `find  target/ -name "wiremock-extensions-*-jar-with-dependencies.jar" | sort | tail -n 1` wiremock-extensions.jar

build: prepare
	docker build -t wiremock-base:2.7.1.latest .