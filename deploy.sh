#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# != 3 ]]; then
    echo "usage: $0 <env> <project> <timestamp>"
    echo "  env        dev | prod"
    echo "  project    cms-hbk | hbd"
    echo "  timestamp  yyyy-mm-ddThh-mm"
    exit 1
fi
env=$1
shift
project=$1
shift
timestamp=$1

execdir="$(
    pushd "$(dirname "$0")" >/dev/null
    pwd
    popd >/dev/null
)"
assemblydir="$(
    pushd "${execdir}/assembly/target/dependency" >/dev/null
    pwd
    popd >/dev/null
)"
if [[ ! -d "${execdir}/../releases" ]]; then
    mkdir "${execdir}/../releases"
fi
releasedir="$(
    pushd "${execdir}/../releases" >/dev/null
    pwd
    popd >/dev/null
)"

ARTIFACTS=()
case "${project}" in
    cms-hbk)
        ARTIFACTS=("wbh.bookworm.hoerbuchkatalog.deployment" "wbh.bookworm.cms.assembly")
        ;;
    hbd)
        ARTIFACTS=("wbh.bookworm.hoerbuchdienst.application.assembly")
        ;;
esac

for artifact in "${!ARTIFACTS[@]}"; do
    echo "Deploying WBH Bookworm ${env} ${artifact}"
    file="${assemblydir}/${artifact}-${timestamp}"
    dir="${releasedir}/${artifact}"
    if [[ ! -f "${dir}" ]]; then
        unzip "${file}.zip" \
            docker-compose.yml docker-compose.${env}.yml wbh-\*.sh \
            -d "${dir}"
    else
        echo "Artifact ${artifact} already exists"
    fi
    pushd "${dir}" >/dev/null
    compose_project="${env}-${artifact}"
    docker-compose \
        -p "${compose_project}" \
        -f docker-compose.yml -f docker-compose.${env}.yml \
        up \
        -d \
        --no-build
    popd >/dev/null
    echo "done"
done

exit 0
