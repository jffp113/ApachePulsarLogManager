#!/usr/bin/env bash

mvn package
kubectl cp ./target/MetricProcessorFunction-1.0-SNAPSHOT.jar pulsar-mini-toolset-0:./
kubectl exec -ti pulsar-mini-toolset-0 -- bash -c "./bin/pulsar-admin functions delete --name MetricProcessorFunction ; \
./bin/pulsar-admin functions create --classname MetricProcessorFunction --jar MetricProcessorFunction-1.0-SNAPSHOT.jar --log-topic persistent://public/default/logging-metrics-processing-function-logs  --inputs persistent://public/default/index_metrics_processing  --tenant public --processing-guarantees ATLEAST_ONCE --retain-key-ordering --parallelism 3 --subs-position Earliest --subs-name metricsSub --namespace default && \
exit
"