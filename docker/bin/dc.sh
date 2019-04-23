#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)

mode=${1:-}
shift

function bookworm_docker() {
    local version=$1 ; shift
    VERSION=${version}; export VERSION
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
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 <version>"
            exit 1
        fi
        bookworm_docker $1 build --compress --force-rm --parallel \
            && bookworm_docker $1 up --no-start
    ;;
    init-data)
        hk=wbhonline_hoerbuchkatalog_1
        admin=wbhonline_admin_1
        pushd ${execdir}/../bkinit >/dev/null
        docker cp conf/secrets.json ${admin}:/opt/bookworm/conf
        for d in hoerbuchkatalog/*.dat
        do
            echo "${d}"
            docker cp -a ${d} ${hk}:/opt/bookworm/var/wbh/hoerbuchkatalog
        done
        for d in hoerbuchkatalog/*.zip
        do
            echo "${d}"
            docker cp -a ${d} ${hk}:/opt/bookworm/var/wbh/hoerbuchkatalog
        done
        for d in nutzerdaten/*.csv
        do
            echo "${d}"
            docker cp -a ${d} ${hk}:/opt/bookworm/var/wbh/nutzerdaten
        done
        popd >/dev/null
        # TODO Start admin container, use scp to copy data
        #docker exec container bash -c 'chmod 664 ${dockervol}/bookworm_app/_data/conf/secrets.json'
        #docker exec container bash -c 'chown 4801:4801 ${dockervol}/bookworm_wbh/_data/hoerbuchkatalog/*'
        #docker exec container bash -c 'chown 4801:4801 ${dockervol}/bookworm_wbh/_data/nutzerdaten/*'
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
                $0 clean volumes
                $0 clean networks
                $0 clean images
                ids=$(docker ps -aq)
                if [[ -n "${ids}" ]]
                then
                    docker rm $(docker ps -aq)
                fi
            ;;
            *)
                echo "usage: $0 clean { containers | volumes | networks | images | all }"
                exit 1
            ;;
        esac
    ;;
    up)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 <version>"
            exit 1
        fi
        bookworm_docker $1 up -d
    ;;
    ps)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 <version>"
            exit 1
        fi
        bookworm_docker $1 ps
    ;;
    restart)
        if [[ $# -lt 2 ]]
        then
            echo "usage: $0 restart <version> <service...>"
            exit 1
        fi
        VERSION=$1 ; shift
        bookworm_docker ${VERSION} restart $*
    ;;
    down)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 down <version>"
            exit 1
        fi
        bookworm_docker $1 down
    ;;
    exec)
        if [[ $# -lt 2 ]]
        then
            echo "usage: $0 exec <version> <docker exec args>"
            exit 1
        fi
        VERSION=$1 ; shift
        bookworm_docker ${VERSION} exec $*
    ;;
    health)
        docker inspect --format='{{json .State.Health}}'
    ;;
    *)
        echo "usage: $0 { init | clean | up | restart | down | exec | health } <version> ..."
        echo "  init <version>"
        echo "  clean { containers | volumes | networks | images | all }"
        echo "  up <version>"
        echo "  restart <version>"
        echo "  down <version>"
        echo "  exec <version> <container> <command>"
        echo "  health"
        exit 1
    ;;
esac

exit 0
