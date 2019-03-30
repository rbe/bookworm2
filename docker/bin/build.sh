#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
. ${platformlibdir}/docker.sh

function show_usage() {
    echo "usage: $0 { <container> | full } <version>"
    echo "    container     datatransfer | hoerbuechkatalog | rproxy"
    exit 1
}

container=${1:-full}
version=${2:-LocalBuild}

case "${container}" in
    admin)
        docker_build_image bookworm admin ${version}
    ;;
    datatransfer)
        docker_build_image bookworm datatransfer ${version}
    ;;
    hoerbuchkatalog)
        docker_build_image bookworm hoerbuchkatalog ${version}
    ;;
    rproxy)
        docker_build_image bookworm rproxy ${version}
    ;;
    full)
        $0 admin ${version}
        $0 datatransfer ${version}
        $0 hoerbuchkatalog ${version}
        $0 rproxy ${version}
    ;;
    *)
        show_usage
    ;;
esac

exit 0
