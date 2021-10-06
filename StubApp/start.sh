#!/bin/sh

docker context create host --docker "host=tcp://host.docker.internal:2375"
docker context use host
docker logs --since 1m -f crossviewer-v3-docker-final_server-v2_1 >> ./logs/server.log

#cp ./server.log ./logs/server.log
#while true; do sleep 600; done