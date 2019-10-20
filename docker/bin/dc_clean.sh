#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
. ${platformlibdir}/docker.sh

mode=${1:-}
shift
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
esac

exit 0
