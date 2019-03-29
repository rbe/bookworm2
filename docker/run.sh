#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

declare MOUNT_TPL="--mount type=volume,volume-driver=local"

function exit_if {
    if [[ $1 -gt 0 ]]
    then
        ret=$1
        shift
        echo $*
        exit ${ret}
    fi
}

function docker_check_vol() {
    local vol=$1
    sudo docker inspect ${vol} 2>/dev/null
    if [[ $? -eq 1 ]]
    then
        sudo docker volume create -d local ${vol} >/dev/null
    fi
    sudo docker inspect ${vol} >/dev/null
    exit_if $? "Docker volume ${vol} not found"
}

function docker_check_network() {
    sudo docker network inspect public 2>/dev/null
    if [[ $? = 1 ]]
    then
        sudo docker network create \
            -d bridge \
            --attachable \
            public \
            >/dev/null
    fi
    sudo docker network inspect private 2>/dev/null
    if [[ $? = 1 ]]
    then
        sudo docker network create \
            -d bridge \
            --attachable \
            --internal \
            private \
            >/dev/null
    fi
}

function show_usage() {
    echo "usage: $0 { network | <container> | full } <version>"
    exit 1
}

mode=${1:-}
shift
case "${mode}" in
    datatransfer)
        [[ $# -lt 1 ]] && show_usage
        version=$1
        docker_check_network
        docker_check_vol datatransfer_etc_ssh
        sudo docker run \
            -d \
            -p 2201:22 \
            --restart=always \
            ${MOUNT_TPL},src=datatransfer_etc_ssh,dst=/etc/ssh \
            ${MOUNT_TPL},src=var_bookworm_templates,dst=/var/bookworm/templates \
            ${MOUNT_TPL},src=var_bookworm_wbh,dst=/var/bookworm/wbh \
            --name bookworm-datatransfer \
            wbh/datatransfer:${version}
    ;;
    hoerbuchkatalog)
        [[ $# -lt 1 ]] && show_usage
        version=$1
        docker_check_network
        docker_check_vol opt_bookworm
        docker_check_vol var_bookworm_templates
        docker_check_vol var_bookworm_repository
        docker_check_vol var_bookworm_wbh
        docker_check_vol var_bookworm_blista
        sudo docker run \
            -d \
            -p 9080:9080 \
            --restart=always \
            ${MOUNT_TPL},src=opt_bookworm,dst=/opt/bookworm \
            ${MOUNT_TPL},src=var_bookworm_templates,dst=/var/bookworm/templates \
            ${MOUNT_TPL},src=var_bookworm_repository,dst=/var/bookworm/repository \
            ${MOUNT_TPL},src=var_bookworm_wbh,dst=/var/bookworm/wbh \
            ${MOUNT_TPL},src=var_bookworm_blista,dst=/var/bookworm/blista \
            --name bookworm-hoerbuchkatalog \
            wbh/hoerbuchkatalog:${version}
    ;;
    rproxy)
        [[ $# -lt 1 ]] && show_usage
        version=$1
        docker_check_network
        docker_check_vol rproxy_etc_nginx
        sudo docker run \
            -d \
            -p 80:80 \
            -p 443:443 \
            --restart=always \
            ${MOUNT_TPL},src=rproxy_etc_nginx,dst=/etc/nginx \
            --name bookworm-rproxy \
            wbh/rproxy:${version}
    ;;
    full)
        $0 datatransfer
        $0 hoerbuchkatalog
        $0 rproxy
    ;;
    *)
        show_usage
    ;;
esac

exit 0
