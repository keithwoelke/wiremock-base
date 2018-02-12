#!/usr/bin/env groovy

node('master') {
    stage("Checkout") {
        checkout scm
    }

    stash name: 'scm', includes: '__files/,mappings/,src/,.git/**,Dockerfile,pom.xml,wiremock-standalone-*.jar', useDefaultExcludes: false
}

node('maven') {
    unstash 'scm'

    stage("Build Extensions") {
        sh "mvn clean package"
        sh "cp `find  target/ -name \"wiremock-extensions-*-jar-with-dependencies.jar\" | sort | tail -n 1` wiremock-extensions.jar"
    }

    stash name: 'prepared', includes: '__files/,mappings/,.git/**,Dockerfile,wiremock-standalone-*.jar,wiremock-extensions.jar', useDefaultExcludes: false
}

node('docker') {
    unstash 'prepared'

    stage("Build Docker Container") {
    }
}
