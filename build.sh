#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

PROJECT=~/project/wbh.bookworm
REPO=artofcoding/bookworm2.git
BRANCH=develop

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
. ${platformlibdir}/git.sh
. ${platformlibdir}/maven.sh

mode=${1:-full}
echo "Building ${mode}"

case "${mode}" in
    modules)
        update_repo ${REPO} ${PROJECT} \
            && pushd ${PROJECT} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog \
            && popd >/dev/null
    ;;
    report)
        update_repo ${REPO} ${PROJECT} \
            && pushd ${PROJECT} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog,bookworm.security,bookworm.staticanalysis \
            && popd >/dev/null
    ;;
    documentation)
        update_repo ${REPO} ${PROJECT} \
            && pushd ${PROJECT} >/dev/null \
            && mvnw_build bookworm.documentation \
            && popd >/dev/null
    ;;
    assembly)
        update_repo ${REPO} ${PROJECT} \
            && pushd ${PROJECT} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog,bookworm.assembly \
            && popd >/dev/null
    ;;
    full)
        update_repo ${REPO} ${PROJECT} \
            && pushd ${PROJECT} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog,bookworm.security,bookworm.staticanalysis,bookworm.documentation,bookworm.assembly \
            && popd >/dev/null
    ;;
    docker)
        $0 full
        ${PROJECT}/docker/bin/build.sh
    ;;
    *)
        echo "usage: $0 { modules | report | documentation | assembly | docker | full }"
        exit 1
    ;;
esac

exit 0
