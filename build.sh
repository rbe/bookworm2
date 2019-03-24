#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

PROJECT=~/project/wbh.bookworm
REPO=artofcoding/bookworm2.git
BRANCH=develop

function update_repo() {
    if [[ ! -d ${PROJECT} ]]
    then
        git clone \
            git@bitbucket.org:${REPO} \
            ${PROJECT}
    else
        git stash
        git pull
    fi
}

function build()    {
    local profiles=$1
    ./mvnw -s settings.xml \
        -Dmaven.repo.local=$(pwd)/.mvn/repository \
        -Dmaven.artifact.threads=10 \
        -P ${profiles} \
        -T 4 \
        clean install
}

mode=${1:-full}
case "${mode}" in
    modules)
        update_repo \
            && pushd ${PROJECT} >/dev/null \
            && build aoc.platform,bookworm.hoerbuchkatalog \
            && popd >/dev/null
    ;;
    report)
        update_repo \
            && pushd ${PROJECT} >/dev/null \
            && build aoc.platform,bookworm.hoerbuchkatalog,bookworm.security,bookworm.staticanalysis \
            && popd >/dev/null
    ;;
    documentation)
        update_repo \
            && pushd ${PROJECT} >/dev/null \
            && build bookworm.documentation \
            && popd >/dev/null
    ;;
    assembly)
        update_repo \
            && pushd ${PROJECT} >/dev/null \
            && build aoc.platform,bookworm.hoerbuchkatalog,bookworm.assembly \
            && popd >/dev/null
    ;;
    docker)
        update_repo \
            && pushd ${PROJECT} >/dev/null \
            && ( cd docker && ./build.sh 1 ) \
            && popd >/dev/null
    ;;
    full)
        update_repo \
            && pushd ${PROJECT} >/dev/null \
            && build aoc.platform,bookworm.hoerbuchkatalog,bookworm.security,bookworm.staticanalysis,bookworm.documentation,bookworm.assembly \
            && ( cd docker && ./build.sh 1 ) \
            && popd >/dev/null
    ;;
    *)
        echo "usage: $0 { modules | report | documentation | assembly | docker | full }"
        exit 1
    ;;
esac

exit 0
