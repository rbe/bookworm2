#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
dockerdir=$(pushd ${execdir}/.. >/dev/null ; pwd ; popd >/dev/null)

if [[ ! -f .env && -z "${VERSION:-}" ]]
then
    echo "Please set VERSION, e.g. export VERSION=LocalBuild or use .env file"
    exit 1
fi

mode=${1:-}
shift

function bookworm_docker() {
    docker-compose \
        -p ${dockerdir}/wbhonline \
        -f ${dockerdir}/cms/docker-compose.yml \
        -f ${dockerdir}/bookworm/docker-compose.yml \
        -f ${dockerdir}/rproxy/docker-compose.yml \
        $*
}

case "${mode}" in
    clean)
        platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
        . ${platformlibdir}/docker.sh
        mode=${1:-}
        case "${mode}" in
            containers)
                docker_clean_containers bookworm
                docker_clean_containers wbhonline
            ;;
            volumes)
                docker_clean_volumes
            ;;
            networks)
                docker_clean_networks
            ;;
            images)
                docker_clean_images bookworm
                docker_clean_images wbhonline
            ;;
            all)
                $0 clean containers
                ids=$(docker ps -aq)
                if [[ -n "${ids}" ]]
                then
                    docker rm $(docker ps -aq)
                fi
                $0 clean volumes
                $0 clean networks
                $0 clean images
            ;;
            *)
                echo "usage: $0 clean { containers | volumes | networks | images | all }"
                exit 1
            ;;
        esac
    ;;
    start)
        bookworm_docker start
    ;;
    stop)
        bookworm_docker stop
    ;;
    restart)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 restart <container ...>"
            exit 1
        fi
        bookworm_docker restart $*
    ;;
    ps)
        bookworm_docker ps
    ;;
    logs)
        bookworm_docker logs $*
    ;;
    down)
        bookworm_docker down
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
    health)
        docker inspect --format='{{json .State.Health}}'
    ;;
    recreate-all)
        $0 down
        $0 clean all
        $0 build
        $0 init
        $0 start
    ;;
    *)
        echo "usage: $0 { clean | start | stop | restart | down | exec | console | health | recreate-all }"
        echo "  clean { containers | volumes | networks | images | all }"
        echo "  start"
        echo "  stop"
        echo "  restart"
        echo "  down"
        echo "  exec <container> <command>"
        echo "  console <container>"
        echo "  health"
        exit 1
    ;;
esac

exit 0
