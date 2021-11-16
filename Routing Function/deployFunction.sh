#!/usr/bin/env bash

mvn package
kubectl cp ./target/RoutingFunction-1.0-SNAPSHOT.jar pulsar-mini-toolset-0:./
kubectl exec -ti pulsar-mini-toolset-0 -- bash -c "./bin/pulsar-admin functions delete --name RoutingFunction ; \
./bin/pulsar-admin functions create --classname RoutingFunction --jar RoutingFunction-1.0-SNAPSHOT.jar --log-topic persistent://public/default/logging-routing-function-logs  --inputs persistent://public/default/parsed_logs  --tenant public --processing-guarantees EFFECTIVELY_ONCE --subs-position Earliest --subs-name routingSub --namespace default --ram 512000000 && \
exit
"
