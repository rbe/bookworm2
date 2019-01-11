#!/usr/bin/env bash

docker run \
    -d \
    -p 80:9080 \
    --restart=always \
    --name hoerbuchkatalog \
    hoerbuchkatalog

exit $?
