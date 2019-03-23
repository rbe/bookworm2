#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

if [[ $# -lt 1 ]]
then
    echo "usage: $0 <version>"
    exit 1
fi

VERSION=$1
set -o nounset

build_docker_image() {
    local name=$1
    local version=$2
    local basedir=${3:-wbh-$name}
    echo "*"
    echo "* Building Docker image version wbh/${name}:${version}"
    echo "* Using $(pwd)/${basedir} as base directory"
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
        | gzip -9 >wbh-${name}-${version}.tar.gz
    if [[ $? != 0 ]]
    then
        echo "Saving image of wbh/${name} failed"
        exit 1
    fi
}

build_docker_image datatransfer ${VERSION}
save_docker_image datatransfer ${VERSION}

build_docker_image rproxy ${VERSION}
save_docker_image rproxy ${VERSION}

build_docker_image hoerbuchkatalog ${VERSION} ../hoerbuchkatalog
save_docker_image hoerbuchkatalog ${VERSION}

echo "* Done"

exit $?
