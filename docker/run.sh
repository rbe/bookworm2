#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
libdir=$(pushd ${execdir}/lib >/dev/null ; pwd ; popd >/dev/null)
. ${libdir}/docker.sh

PRIVNET=192.168.48

function show_usage() {
    echo "usage: $0 { <container> | all } <version>"
    echo "    container     datatransfer | hoerbuechkatalog | rproxy"
    exit 1
}

mode=${1:-all}
version=${2:-LocalBuild}

case "${mode}" in
    datatransfer)
        docker_check_network
        docker_check_vol datatransfer_etc_ssh
        docker_check_vol var_bookworm_templates
        docker_check_vol var_bookworm_wbh
        sudo docker run \
            -d \
            --network public \
            --name bookworm-datatransfer \
            --hostname bookworm-datatransfer \
            -p 2201:22 \
            --restart=always \
            ${MOUNT_TPL},src=datatransfer_etc_ssh,dst=/etc/ssh \
            ${MOUNT_TPL},src=var_bookworm_templates,dst=/var/bookworm/templates \
            ${MOUNT_TPL},src=var_bookworm_wbh,dst=/var/bookworm/wbh \
            bookworm/datatransfer:${version}
    ;;
    hoerbuchkatalog)
        docker_check_network
        docker_check_vol opt_bookworm
        docker_check_vol var_bookworm_templates
        docker_check_vol var_bookworm_repository
        docker_check_vol var_bookworm_wbh
        docker_check_vol var_bookworm_blista
        sudo docker run \
            -d \
            --network private \
            --name bookworm-hoerbuchkatalog \
            --hostname bookworm-hoerbuchkatalog \
            --ip ${PRIVNET}.3 \
            --restart=always \
            ${MOUNT_TPL},src=opt_bookworm,dst=/opt/bookworm \
            ${MOUNT_TPL},src=var_bookworm_templates,dst=/var/bookworm/templates \
            ${MOUNT_TPL},src=var_bookworm_repository,dst=/var/bookworm/repository \
            ${MOUNT_TPL},src=var_bookworm_wbh,dst=/var/bookworm/wbh \
            ${MOUNT_TPL},src=var_bookworm_blista,dst=/var/bookworm/blista \
            bookworm/hoerbuchkatalog:${version}
    ;;
    rproxy)
        docker_check_network
        docker_check_vol rproxy_etc_nginx
        sudo docker run \
            -d \
            --network private \
            --name bookworm-rproxy \
            --hostname bookworm-rproxy \
            --ip ${PRIVNET}.2 \
            -p 80:80 \
            -p 443:443 \
            --restart=always \
            ${MOUNT_TPL},src=rproxy_etc_nginx,dst=/etc/nginx \
            bookworm/rproxy:${version}
        sudo docker network connect public bookworm-rproxy
    ;;
    all)
        $0 datatransfer ${version}
        $0 hoerbuchkatalog ${version}
        $0 rproxy ${version}
    ;;
    *)
        show_usage
    ;;
esac

exit 0
