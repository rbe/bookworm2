#!/usr/bin/env bash

declare MOUNT=--mount type=volume,volume-driver=local

docker volume create -d local var_templates
docker volume create -d local var_wbh
docker run \
    -d \
    -p 9080:9080 \
    --restart=always \
    ${MOUNT},src=var_templates,dst=/var/templates \
    ${MOUNT},src=var_wbh,dst=/var/wbh \
    --name bookworm-hk \
    wbh/hoerbuchkatalog:latest

docker volume create -d local rproxy_etc_nginx
docker run \
    -d \
    -p 80:80 \
    -p 443:443 \
    --restart=always \
    ${MOUNT},src=rproxy_etc_nginx,dst=/etc/nginx \
    --name bookworm-rproxy \
    wbh/rproxy:latest

docker volume create -d local datatransfer_etc_ssh
docker run \
    -d \
    -p 2201:22 \
    --restart=always \
    ${MOUNT},src=datatransfer_etc_ssh,dst=/etc/ssh \
    ${MOUNT},src=var_wbh,dst=/var/wbh \
    --name bookworm-datatransfer \
    wbh/datatransfer:latest

exit $?
