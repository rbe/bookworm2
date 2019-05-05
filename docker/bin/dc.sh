#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)

if [[ -z "${VERSION:-}" ]]
then
    echo "Please set VERSION, e.g. export VERSION=LocalBuild"
    exit 1
fi

mode=${1:-}
shift

function bookworm_docker() {
    pushd ${execdir}/.. >/dev/null
    docker-compose \
        -p wbhonline \
        -f wbhcms/docker-compose.yml \
        -f bookworm/docker-compose.yml \
        -f wbhonline/docker-compose.yml \
        $*
    popd >/dev/null
}

case "${mode}" in
    build)
        prjdir=$(pushd ${execdir}/../../ >/dev/null ; pwd ; popd >/dev/null)
        pushd ${prjdir} >/dev/null
        ./build.sh assembly
        popd >/dev/null
    ;;
    init)
        # --parallel
        bookworm_docker build --compress --force-rm \
            && bookworm_docker up --no-start
        admin=wbhonline_admin_1
        pushd ${execdir}/../bkinit >/dev/null
        docker cp conf/secrets.json ${admin}:/opt/bookworm/conf
        for d in hoerbuchkatalog/*.dat
        do
            echo "${d}"
            docker cp ${d} ${admin}:/opt/bookworm/var/wbh/hoerbuchkatalog
        done
        for d in hoerbuchkatalog/*.zip
        do
            echo "${d}"
            docker cp ${d} ${admin}:/opt/bookworm/var/wbh/hoerbuchkatalog
        done
        for d in nutzerdaten/*.csv
        do
            echo "${d}"
            docker cp ${d} ${admin}:/opt/bookworm/var/wbh/nutzerdaten
        done
        popd >/dev/null
    ;;
    clean)
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
        bookworm_docker start admin
        bookworm_docker exec admin chmod 660 /opt/bookworm/conf/secrets.json
        bookworm_docker exec admin chown bookworm:bookworm /opt/bookworm/conf/secrets.json
        bookworm_docker exec admin chown -R bookworm:bookworm /opt/bookworm/var/wbh/hoerbuchkatalog
        bookworm_docker exec admin chmod -R 660 /opt/bookworm/var/wbh/hoerbuchkatalog/
        bookworm_docker exec admin chmod -R 770 /opt/bookworm/var/wbh/hoerbuchkatalog
        bookworm_docker exec admin chown -R bookworm:bookworm /opt/bookworm/var/wbh/nutzerdaten
        bookworm_docker exec admin chmod -R 660 /opt/bookworm/var/wbh/nutzerdaten/
        bookworm_docker exec admin chmod 770 /opt/bookworm/var/wbh/nutzerdaten
        bookworm_docker start
    ;;
    stop)
        bookworm_docker stop
    ;;
    ps)
        bookworm_docker ps
    ;;
    logs)
        bookworm_docker logs $1
    ;;
    restart)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 restart <service...>"
            exit 1
        fi
        bookworm_docker restart $*
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
            echo "usage: $0 console <service>"
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
        echo "usage: $0 { init | clean | start | stop | restart | down | exec | health }"
        echo "  init"
        echo "  copy-data"
        echo "  clean { containers | volumes | networks | images | all }"
        echo "  start"
        echo "  stop"
        echo "  restart"
        echo "  down"
        echo "  exec <container> <command>"
        echo "  health"
        exit 1
    ;;
esac

exit 0
