#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)

if [[ ! -f .env && -z "${VERSION:-}" ]]
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
        -f cms/docker-compose.yml \
        -f bookworm/docker-compose.yml \
        -f rproxy/docker-compose.yml \
        $*
    popd >/dev/null
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
        # Konfiguration
        bookworm_docker exec admin chmod 660 /opt/bookworm/conf/secrets.json
        bookworm_docker exec admin chown bookworm:bookworm /opt/bookworm/conf/secrets.json
        # Daten - Hörbuchkatalog
        bookworm_docker exec admin chown -R bookworm:bookworm /opt/bookworm/var/wbh/hoerbuchkatalog
        bookworm_docker exec admin chmod -R 660 /opt/bookworm/var/wbh/hoerbuchkatalog/
        bookworm_docker exec admin chmod -R 770 /opt/bookworm/var/wbh/hoerbuchkatalog
        bookworm_docker exec admin chmod -R 660 /opt/bookworm/var/repository/Bestellung/*
        bookworm_docker exec admin chmod -R 660 /opt/bookworm/var/repository/Merkliste/*
        # Daten - Nutzerdaten
        bookworm_docker exec admin chown -R bookworm:bookworm /opt/bookworm/var/wbh/nutzerdaten
        bookworm_docker exec admin chmod -R 660 /opt/bookworm/var/wbh/nutzerdaten/
        bookworm_docker exec admin chmod 770 /opt/bookworm/var/wbh/nutzerdaten
        # Aktualisierung der Daten
        bookworm_docker exec admin mkdir /opt/bookworm/var/wbh/aktualisierung
        bookworm_docker exec admin chown root:root /opt/bookworm/var/wbh/aktualisierung
        bookworm_docker exec admin chmod 555 /opt/bookworm/var/wbh/aktualisierung
        bookworm_docker exec admin mkdir /opt/bookworm/var/wbh/aktualisierung/eingangskorb
        bookworm_docker exec admin chown bookworm:bookworm /opt/bookworm/var/wbh/aktualisierung/eingangskorb
        bookworm_docker exec admin chmod 770 /opt/bookworm/var/wbh/aktualisierung/eingangskorb
        #
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
        echo "usage: $0 { assembly | init | clean | start | stop | restart  | ps | logs }"
        exit 1
    ;;
esac

exit 0
