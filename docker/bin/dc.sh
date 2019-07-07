#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

if [[ $(basename $0) == dcenv.sh ]]
then
    echo "Please do not call this script directly."
    echo "Use a link:"
    echo "  ln -s dc.sh <envname>.sh and create <envname>.env"
    exit 1
fi

if [[ $# -lt 1 ]]
then
    echo "usage: $0 { assembly }"
    echo "usage: $0 { up | start | stop | restart | down | ps | logs | exec | console }"
    echo "  restart <service>"
    echo "  exec <service> <command>"
    echo "  console <service>"
    exit 1
fi

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
etcdir=$(pushd ${execdir}/../etc >/dev/null ; pwd ; popd >/dev/null)
dockerdir=$(pushd ${execdir}/.. >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)

ENV_NAME=$(expr `basename $0` : '\(.*\).sh')
echo "Environment: ${ENV_NAME}"
. ${etcdir}/${ENV_NAME}.env

export ENV_NAME
export VERSION
export CMS_BACKEND_NET
export HOERBUCHKATALOG_BACKEND_NET
export RPROXY_IP
export RPROXY_PUBLIC_HTTP_PORT
export RPROXY_PUBLIC_HTTPS_PORT
export JOOMLA_IP
export JOOMLA_PORT
export JOOMLADB_IP
export JOOMLADB_PORT
export ASSETS_IP
export ASSETS_PUBLIC_PORT
#export VAULT_IP
#export VAULT_PORT
export HOERBUCHKATALOG_IP
export HOERBUCHKATALOG_PORT
export DATATRANSFER_PUBLIC_PORT
export ADMIN_PUBLIC_PORT

function ask_continue() {
    read -N 1 -t 5 -p "Continue? [auto in 5 secs=Y/n] " yn
    echo
    if [[ "${yn}" == "n" && ${yn} == "N" ]]
    then
        exit 1
    fi
}

function bookworm_docker() {
    docker-compose \
        -p ${ENV_NAME} \
        -f ${dockerdir}/cms/docker-compose.yml \
        -f ${dockerdir}/hoerbuchkatalog/docker-compose.yml \
        -f ${dockerdir}/rproxy/docker-compose.yml \
        $*
}

#echo "Reverse Proxy HTTP Port=${RPROXY_PUBLIC_HTTP_PORT}"
#echo "Reverse Proxy HTTPS Port=${RPROXY_PUBLIC_HTTPS_PORT}"
#echo "CMS Assets SSH=${ASSETS_PUBLIC_PORT}"
#echo "Hörbuchkatalog WBH Datatransfer SSH=${DATATRANSFER_PUBLIC_PORT}"
#echo "Hörbuchkatalog Admin SSH=${ADMIN_PUBLIC_PORT}"
#echo

mode=${1:-}
shift
case "${mode}" in
    assembly)
        prjdir=$(pushd ${execdir}/../../ >/dev/null ; pwd ; popd >/dev/null)
        pushd ${prjdir} >/dev/null
        ./build.sh assembly
        popd >/dev/null
    ;;
    build-images)
        docker build \
            -t wbhonline/sshd-alpine:$(cat ${dockerdir}/sshd-alpine/.version) \
            ${dockerdir}/sshd-alpine
        ask_continue
        #--force-rm --no-cache
        bookworm_docker build \
            --build-arg ENV_NAME=${ENV_NAME} \
            --compress
    ;;
    up)
        bookworm_docker up -d
    ;;
    start)
        bookworm_docker up --no-start
        bookworm_docker start admin
        bookworm_docker exec admin /opt/bookworm/bin/mkdirs.sh
        bookworm_docker exec admin /opt/bookworm/bin/perms.sh
        if [[ -f conf/secrets.json ]]
        then
            docker cp conf/secrets.json ${ENV_NAME}_admin_1:/opt/bookworm/conf
        fi
        bookworm_docker start joomla-db
        sleep 10
        bookworm_docker start joomla
        sleep 10
        phpfpm_pool_conf=${dockerdir}/cms/www-${ENV_NAME}.conf
        if [[ -f ${phpfpm_pool_conf} ]]
        then
            docker cp ${phpfpm_pool_conf} ${ENV_NAME}_joomla_1:/usr/local/etc/php-fpm.d/www.conf
        fi
        bookworm_docker start
    ;;
    stop)
        bookworm_docker stop
    ;;
    restart)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 restart <service ...>"
            exit 1
        fi
        bookworm_docker restart $*
    ;;
    down)
        bookworm_docker down
    ;;
    ps)
        bookworm_docker ps
    ;;
    logs)
        bookworm_docker logs $*
    ;;
    exec)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 exec <docker exec args>"
            exit 1
        fi
        bookworm_docker exec $*
    ;;
    console)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 console <container>"
            exit 1
        fi
        bookworm_docker exec $1 sh
    ;;
    console-broken)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 console-broken <container>"
            exit 1
        fi
        container_status=$(docker ps -a --filter name=$1 --format '{{.Status}}')
        docker commit $1 $1_broken && docker run -it $1_broken sh
    ;;
    clear-logs)
        find /var/lib/docker/containers/ -type f -name "*.log" -delete
    ;;
    health)
        docker inspect --format='{{json .State.Health}}'
    ;;
    compose)
        bookworm_docker $*
    ;;
esac
