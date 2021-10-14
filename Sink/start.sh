#!/bin/sh

while [ "$(curl -s -o /dev/null -w ''%{http_code}'' http://pulsar-mini-proxy:80/status.html)" != "200" ]; do sleep 5; done

java -jar ./target/PostgresSink-1.0-SNAPSHOT-jar-with-dependencies.jar