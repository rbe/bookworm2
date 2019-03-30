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
    admin)
        docker_check_network
        docker_check_vol admin_etc_ssh
        docker_check_vol bookworm_templates
        docker_check_vol bookworm_repository
        docker_check_vol bookworm_wbh
        docker_check_vol bookworm_blista
        sudo docker run \
            -d \
            --network public \
            --name bookworm-admin \
            --hostname bookworm-admin \
            -p 2202:22 \
            --restart=always \
            ${MOUNT_TPL},src=admin_etc_ssh,dst=/etc/ssh \
            ${MOUNT_TPL},src=bookworm_templates,dst=/opt/bookworm/vartemplates \
            ${MOUNT_TPL},src=bookworm_repository,dst=/opt/bookworm/varrepository \
            ${MOUNT_TPL},src=bookworm_wbh,dst=/opt/bookworm/varwbh \
            ${MOUNT_TPL},src=bookworm_blista,dst=/opt/bookworm/varblista \
            bookworm/datatransfer:${version}
    ;;
    datatransfer)
        docker_check_network
        docker_check_vol datatransfer_etc_ssh
        docker_check_vol bookworm_templates
        docker_check_vol bookworm_wbh
        sudo docker run \
            -d \
            --network public \
            --name bookworm-datatransfer \
            --hostname bookworm-datatransfer \
            -p 2201:22 \
            --restart=always \
            ${MOUNT_TPL},src=datatransfer_etc_ssh,dst=/etc/ssh \
            ${MOUNT_TPL},src=bookworm_templates,dst=/opt/bookworm/var/templates \
            ${MOUNT_TPL},src=bookworm_wbh,dst=/opt/bookworm/var/wbh \
            bookworm/datatransfer:${version}
    ;;
    hoerbuchkatalog)
        docker_check_network
        docker_check_vol opt_bookworm
        docker_check_vol bookworm_templates
        docker_check_vol bookworm_repository
        docker_check_vol bookworm_wbh
        docker_check_vol bookworm_blista
        sudo docker run \
            -d \
            --network private \
            --name bookworm-hoerbuchkatalog \
            --hostname bookworm-hoerbuchkatalog \
            --ip ${PRIVNET}.3 \
            --restart=always \
            ${MOUNT_TPL},src=opt_bookworm,dst=/opt/bookworm \
            ${MOUNT_TPL},src=bookworm_templates,dst=/opt/bookworm/var/templates \
            ${MOUNT_TPL},src=bookworm_repository,dst=/opt/bookworm/var/repository \
            ${MOUNT_TPL},src=bookworm_wbh,dst=/opt/bookworm/var/wbh \
            ${MOUNT_TPL},src=bookworm_blista,dst=/opt/bookworm/var/blista \
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
