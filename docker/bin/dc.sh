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
        -p wbhonline \
        -f ${dockerdir}/cms/docker-compose.yml \
        -f ${dockerdir}/bookworm/docker-compose.yml \
        -f ${dockerdir}/rproxy/docker-compose.yml \
        $*
}

case "${mode}" in
    assembly)
        prjdir=$(pushd ${execdir}/../../ >/dev/null ; pwd ; popd >/dev/null)
        pushd ${prjdir} >/dev/null
        ./build.sh assembly
        popd >/dev/null
    ;;
    init)
        bookworm_docker build --compress --force-rm \
            && bookworm_docker up --no-start
        admin=wbhonline_admin_1
        pushd ${execdir}/../bkinit >/dev/null
        docker cp conf/secrets.json ${admin}:/opt/bookworm/conf
        popd >/dev/null
    ;;
    start)
        bookworm_docker start admin
        bookworm_docker exec admin /opt/bookworm/mkdirs.sh
        bookworm_docker exec admin /opt/bookworm/perms.sh
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
    *)
        echo "usage: $0 { assembly | init | start | stop | restart | ps | logs }"
        exit 1
    ;;
esac

exit 0
