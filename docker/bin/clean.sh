#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
. ${platformlibdir}/docker.sh

mode=${1:-}
case "${mode}" in
    containers)
        docker_clean_containers bookworm
    ;;
    volumes)
        docker_clean_volumes
    ;;
    networks)
        docker_clean_networks
    ;;
    images)
        docker_clean_images bookworm
    ;;
    all)
        $0 containers
        $0 volumes
        $0 networks
        $0 images
    ;;
    *)
        echo "usage: $0 { containers | volumes | networks | images | all }"
        exit 1
    ;;
esac

exit 0
