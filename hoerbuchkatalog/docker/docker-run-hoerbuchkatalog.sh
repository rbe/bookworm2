#!/usr/bin/env bash

docker volume create -d local templates
docker volume create -d local var_hoerbuchkatalog
docker volume create -d local var_nutzerdaten

docker run \
    -d \
    -p 80:9080 \
    --restart=always \
    --name wbh/hoerbuchkatalog \
    --mount type=volume,src=templates,dst=/templates,volume-driver=local \
    --mount type=volume,src=var_hoerbuchkatalog,dst=/var/hoerbuchkatalog,volume-driver=local \
    --mount type=volume,src=var_nutzerdaten,dst=/var/nutzerdaten,volume-driver=local \
    hoerbuchkatalog

exit $?
