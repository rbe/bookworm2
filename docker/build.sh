#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
libdir=$(pushd ${execdir}/lib >/dev/null ; pwd ; popd >/dev/null)
. ${libdir}/docker.sh

function show_usage() {
    echo "usage: $0 { <container> | full } <version>"
    echo "    container     datatransfer | hoerbuechkatalog | rproxy"
    exit 1
}

container=${1:-full}
version=${2:-LocalBuild}

case "${container}" in
    admin)
        docker_build_image admin ${version}
    ;;
    datatransfer)
        docker_build_image datatransfer ${version}
    ;;
    rproxy)
        docker_build_image rproxy ${version}
    ;;
    hoerbuchkatalog)
        docker_build_image hoerbuchkatalog ${version}
    ;;
    full)
        $0 datatransfer ${version}
        $0 rproxy ${version}
        $0 hoerbuchkatalog ${version}
    ;;
    *)
        show_usage
    ;;
esac

exit 0
