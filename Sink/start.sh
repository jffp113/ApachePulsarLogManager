#!/bin/sh

while [ "$(curl -s -o /dev/null -w ''%{http_code}'' http://pulsar-mini-proxy:80/status.html)" != "200" ]; do sleep 5; done

java -javaagent:./jmx_prometheus_javaagent-0.16.1.jar=9000:config.yaml -jar ./target/PostgresSink-1.0-SNAPSHOT-jar-with-dependencies.jar
