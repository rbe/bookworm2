#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

declare MOUNT_TPL="--mount type=volume,volume-driver=local"

function exit_if {
    if [[ $1 -gt 0 ]]
    then
        ret=$1
        shift
        echo "$@"
        exit ${ret}
    fi
}

function docker_build_image() {
    local vendor=$1
    local name=$2
    local version=$3
    local tag=${vendor}/${name}:${version}
    # TODO basedir not always ../..
    local basedir=$(pushd ../.. >/dev/null ; pwd ; popd >/dev/null)
    echo "*"
    echo "* Building Docker image ${tag}"
    echo "* Using base directory ${basedir}"
    echo "*"
    # experimental --squash \
    # TODO Dockerfile not always in ../${vendor}-${name}
    docker build \
        --rm \
        --tag ${tag} \
        -f ../${vendor}-${name}/Dockerfile ${basedir}
    if [[ $? != 0 ]]
    then
        echo "Building image ${tag} failed"
        exit 1
    fi
}

function docker_save_image() {
    local vendor=$1
    local name=$2
    local version=$3
    local tag=${vendor}/${name}:${version}
    local archive=${vendor}-${name}-${version}
    echo "*"
    echo "* Saving Docker image ${tag}"
    echo "*"
    docker save ${tag} \
        | gzip -9 >${archive}.tar.gz
    if [[ $? != 0 ]]
    then
        echo "Saving image of ${tag} failed"
        exit 1
    fi
}

function docker_check_vol() {
    local vol=$1
    docker inspect ${vol} 2>&1 >/dev/null
    if [[ $? -eq 1 ]]
    then
        docker volume create -d local ${vol} 2>&1 >/dev/null
    fi
    docker inspect ${vol} 2>&1 >/dev/null
    exit_if $? "Docker volume ${vol} not found"
}

function docker_check_private_network() {
    local name=$1
    local subnet=$2
    docker network inspect ${name} 2>&1 >/dev/null
    if [[ $? = 1 ]]
    then
        docker network create \
            -d bridge \
            --internal \
            --subnet=${subnet} \
            --attachable \
            ${name} \
            2>&1 >/dev/null
    fi
}

function docker_check_public_network() {
    local name=$1
    docker network inspect ${name} 2>&1 >/dev/null
    if [[ $? = 1 ]]
    then
        docker network create \
            -d bridge \
            --attachable \
            ${name} \
            2>&1 >/dev/null
    fi
}

function docker_clean_containers() {
    local prefix=$1
    local ids=$(docker ps -aqf name="${prefix}*")
    if [[ ${#ids} -gt 0 ]]
    then
        docker rm -f ${ids}
    fi
}

function docker_clean_volumes() {
    docker volume prune -f
}

function docker_clean_networks() {
    docker network prune -f
}

function docker_clean_images() {
    local t=$1
    local ids=$(docker image ls -qf dangling=true)
    if [[ ${#ids} -gt 0 ]]
    then
        docker image rm ${ids}
    fi
    ids=$(docker image ls -qf reference="${t}/*:*")
    if [[ ${#ids} -gt 0 ]]
    then
        docker image rm ${ids}
    fi
    docker image prune -f
}

function archlinux_install_docker() {
    # docker
    sudo pacman -Qi docker 2>&1 >/dev/null
    if [[ $? = 1 ]]
    then
        # Docker needs iptables 1.8.0 for "docker run -p"
        sudo pacman --noconfirm -U \
            https://archive.archlinux.org/repos/2018/11/15/core/os/x86_64/iptables-1:1.8.0-1-x86_64.pkg.tar.xz
        sudo pacman --noconfirm -S docker
    fi
    sudo groupadd docker
    sudo systemctl enable docker
    # docker-compose
    sudo pacman -Qi docker 2>&1 >/dev/null
    if [[ $? = 1 ]]
    then
        pacman --noconfirm -S docker-compose
    fi
    systemctl enable docker
    systemctl start docker
    docker info
}
