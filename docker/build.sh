#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

build_docker_image() {
    local name=$1
    local version=$2
    local basedir=$(pushd .. >/dev/null ; pwd ; popd >/dev/null)
    local tag=bookworm/${name}:${version}
    echo "*"
    echo "* Building Docker image version ${tag}"
    echo "* Using base directory ${basedir}"
    echo "*"
    # experimental --squash \
    sudo docker build \
        --rm \
        --tag ${tag} \
        -f bookworm-${name}/Dockerfile ${basedir}
    if [[ $? != 0 ]]
    then
        echo "Building image ${tag} failed"
        exit 1
    fi
}

save_docker_image() {
    local name=$1
    local version=$2
    local tag=bookworm/${name}:${version}
    local archive=bookworm-${name}-${version}
    echo "*"
    echo "* Saving Docker image version ${archive}"
    echo "*"
    sudo docker save ${tag} \
        | gzip -9 >${archive}.tar.gz
    if [[ $? != 0 ]]
    then
        echo "Saving image of ${tag} failed"
        exit 1
    fi
}

function show_usage() {
    echo "usage: $0 { <container> | full } <version>"
    echo "    container     datatransfer | hoerbuechkatalog | rproxy"
    exit 1
}

CONTAINER=${1:-full}
VERSION=${2:-LocalBuild}

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
        $0 datatransfer ${VERSION}
        $0 rproxy ${VERSION}
        $0 hoerbuchkatalog ${VERSION}
    ;;
    *)
        show_usage
    ;;
esac

exit $?
