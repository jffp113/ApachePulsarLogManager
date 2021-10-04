eval $(minikube docker-env)

docker build -f ./../Sidecar/Dockerfile -t sidecar ./../Sidecar
docker build -f ./../StubApp/Dockerfile -t stubapp ./../StubApp
docker build -f ./../EmailAlarm/Dockerfile -t emailalarm ./../EmailAlarm
docker build -f ./../Sink/Dockerfile -t sink ./../Sink
kubectl apply -f sidecar.yaml
kubectl apply -f sink.yaml
#kubectl apply -f alarms.yaml