#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

REPO=artofcoding/bookworm2.git
BRANCH=develop

#
# DO NOT MODIFY LINES BELOW
#

set -o nounset
set -o errexit

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd "${execdir}/platform/src/main/bash" >/dev/null ; pwd ; popd >/dev/null)
. "${platformlibdir}/git.sh"
. "${platformlibdir}/maven.sh"

mode=${1:-}
echo "Building ${mode}"

case "${mode}" in
    update-repo)
        update_repo ${REPO} ${execdir}
    ;;
    modules)
        pushd ${execdir} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog \
            && popd >/dev/null
    ;;
    report)
        pushd ${execdir} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog,bookworm.security,bookworm.staticanalysis \
            && popd >/dev/null
    ;;
    documentation)
        pushd ${execdir} >/dev/null \
            && mvnw_build bookworm.documentation \
            && popd >/dev/null
    ;;
    assembly)
        pushd ${execdir} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog,bookworm.assembly \
            && popd >/dev/null
    ;;
    full)
        pushd ${execdir} >/dev/null \
            && mvnw_build aoc.platform,bookworm.hoerbuchkatalog,bookworm.security,bookworm.staticanalysis,bookworm.documentation,bookworm.assembly \
            && popd >/dev/null
    ;;
    *)
        echo "usage: $0 { update-repo | modules | report | documentation | assembly | full }"
        exit 1
    ;;
esac

exit 0
