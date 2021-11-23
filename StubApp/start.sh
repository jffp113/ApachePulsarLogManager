#!/bin/sh

#docker context create host --docker "host=tcp://host.docker.internal:2375"
#docker context use host
#docker logs --since 1m -f crossviewer-v3-docker-final_server-v2_1 > ./logs/server.log
#ssh -o StrictHostKeyChecking=no academia@192.168.0.252
#ssh 'academia@192.168.0.252' "docker logs --tail=1 -f crossviewerv3dockerfinal_server-v2_1" > ./logs/server.log

i=0
while true; do
i=$((i+1))
date=$(date '+%Y-%m-%d %H:%M:%S,%3N')
echo "$date INFO [org.jboss.weld.deployer] (MSC service thread 1-6) $i" >> ./logs/server.log;
sleep 0.05;
done

#cp ./server.log ./logs/server.log
#while true; do sleep 600; done
