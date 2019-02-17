#!/usr/bin/env bash

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
    echo "*"
    echo "* Building Docker image version wbh/${name}:${version}"
    echo "*"
    docker build \
        --tag wbh/${name}:${version} \
        --rm \
        -f Dockerfile-${name} ..
    if [[ $? -neq 0 ]]
    then
        echo "Building image failed"
        exit 1
    fi
}

save_docker_image() {
    local name=$1
    local version=$2
    echo "*"
    echo "* Saving Docker image version wbh-${name}-${version}"
    echo "*"
    docker save \
        --output wbh-${name}-${version}.tar \
        wbh/${name}:${version}
    if [[ $? -neq 0 ]]
    then
        echo "Saving image of wbh/${name} failed"
        exit 1
    fi
}

build_docker_image datatransfer ${VERSION}
save_docker_image datatransfer ${VERSION}

build_docker_image rpproxy ${VERSION}
save_docker_image rpproxy ${VERSION}

build_docker_image hoerbuchkatalog ${VERSION}
save_docker_image hoerbuchkatalog ${VERSION}

echo "* Done"
exit $?
