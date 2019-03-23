#!/usr/bin/env bash

declare MOUNT_TPL=--mount type=volume,volume-driver=local

function exit_if {
    if [[ $1 -gt 0 ]]
    then
        shift
        echo $*
        exit $1
    fi
}

function docker_check_vol() {
    local vol=$1
    sudo docker inspect ${vol} >/dev/null
    if [[ $? -eq 1 ]]
    then
        docker volume create -d ${vol}
    fi
    sudo docker inspect ${vol} >/dev/null
    exit_if $? "Docker volume ${vol} not found"
}

sudo docker network create \
    -d bridge \
    --attachable \
    public
sudo docker network create \
    -d bridge \
    --attachable \
    --internal \
    private

docker_check_vol datatransfer_etc_ssh
docker_check_vol var_templates
docker_check_vol var_wbh
docker_check_vol rproxy_etc_nginx

sudo docker run \
    -d \
    -p 2201:22 \
    --restart=always \
    ${MOUNT_TPL},src=datatransfer_etc_ssh,dst=/etc/ssh \
    ${MOUNT_TPL},src=var_wbh,dst=/var/wbh \
    --name bookworm-datatransfer \
    wbh/datatransfer:latest

sudo docker run \
    -d \
    -p 9080:9080 \
    --restart=always \
    ${MOUNT_TPL},src=var_templates,dst=/var/templates \
    ${MOUNT_TPL},src=var_wbh,dst=/var/wbh \
    --name bookworm-hk \
    wbh/hoerbuchkatalog:latest

sudo docker run \
    -d \
    -p 80:80 \
    -p 443:443 \
    --restart=always \
    ${MOUNT_TPL},src=rproxy_etc_nginx,dst=/etc/nginx \
    --name bookworm-rproxy \
    wbh/rproxy:latest

exit 0
