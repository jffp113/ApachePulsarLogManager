#!/usr/bin/env bash

mvn package

kubectl cp ./target/ParserFunction-1.0-SNAPSHOT.jar pulsar-mini-toolset-0:./
kubectl exec -ti pulsar-mini-toolset-0 -- bash -c "./bin/pulsar-admin functions delete --name ParserFunction ; \
./bin/pulsar-admin functions create --classname ParserFunction --jar ParserFunction-1.0-SNAPSHOT.jar --log-topic persistent://public/default/logging-parser-function-logs  --topics-pattern persistent://public/default/rawlogs-dev-xviewer-.*  --tenant public --processing-guarantees EFFECTIVELY_ONCE --subs-position Earliest --subs-name parserSub --namespace default && \
exit
"
