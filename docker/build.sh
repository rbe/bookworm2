#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

build_docker_image() {
    local name=$1
    local version=$2
    local basedir=$(pushd .. >/dev/null ; pwd ; popd >/dev/null)
    echo "*"
    echo "* Building Docker image version wbh/${name}:${version}"
    echo "* Using base directory ${basedir}"
    echo "*"
    # experimental --squash \
    sudo docker build \
        --rm \
        --tag wbh/${name}:${version} \
        -f wbh-${name}/Dockerfile ${basedir}
    if [[ $? != 0 ]]
    then
        echo "Building image ${name}:${version} failed"
        exit 1
    fi
}

save_docker_image() {
    local name=$1
    local version=$2
    echo "*"
    echo "* Saving Docker image version wbh-${name}-${version}"
    echo "*"
    sudo docker save \
        wbh/${name}:${version} \
        | gzip -9 \
        >wbh-${name}-${version}.tar.gz
    if [[ $? != 0 ]]
    then
        echo "Saving image of wbh/${name} failed"
        exit 1
    fi
}

CONTAINER=${1:-full}
VERSION=${2:-LocalBuild}

set -o nounset

case "${CONTAINER}" in
    datatransfer)
        build_docker_image datatransfer ${VERSION}
    ;;
    rproxy)
        build_docker_image rproxy ${VERSION}
    ;;
    hoerbuchkatalog)
        build_docker_image hoerbuchkatalog ${VERSION}
    ;;
    full)
        $0 datatransfer
        $0 rproxy
        $0 hoerbuchkatalog
    ;;
    *)
        echo "usage: $0 { <container> | full } <version>"
    ;;
esac

exit $?
