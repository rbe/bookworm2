#!/usr/bin/env bash

docker build \
    --tag wbh/hoerbuchkatalog:1 \
    --rm \
    -f Dockerfile .

exit $?
