#!/usr/bin/env bash

kubectl cp ./target/FilterFunction-1.0-SNAPSHOT.jar pulsar-mini-toolset-0:./
kubectl exec -ti pulsar-mini-toolset-0 -- bash -c "./bin/pulsar-admin functions delete --name FilterFunction ; \
./bin/pulsar-admin functions create --classname FilterFunction --jar FilterFunction-1.0-SNAPSHOT.jar --inputs persistent://public/default/parsed_logs --output persistent://public/default/severe-logs  --tenant public --processing-guarantees EFFECTIVELY_ONCE --namespace default && \
exit
"
