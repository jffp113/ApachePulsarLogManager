eval $(minikube docker-env)

docker build -f ./../Extractor/Dockerfile -t extractor ./../Extractor
docker build -f ./../StubApp/Dockerfile -t stubapp ./../StubApp
docker build -f ./../EmailAlarm/Dockerfile -t emailalarm ./../EmailAlarm
docker build -f ./../Sink/Dockerfile -t sink ./../Sink
docker build -f ./../MetricsSink/Dockerfile -t metricssink ./../MetricsSink
docker build -f ./../IndexAggregator/Dockerfile -t indexaggregator ./../IndexAggregator

kubectl apply -f xviewer_configmap.yaml
kubectl apply -f extractor.yaml
kubectl apply -f sink.yaml
kubectl apply -f metricssink.yaml
kubectl apply -f alarms.yaml
kubectl apply -f indexerAggregator.yaml
#kubectl apply -f xviewer_configmap.yaml
