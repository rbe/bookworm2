#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
libdir=$(pushd ${execdir}/lib >/dev/null ; pwd ; popd >/dev/null)
. ${libdir}/docker.sh

docker_clean_containers bookworm
docker_clean_volumes
docker_clean_networks
docker_clean_images bookworm

exit 0
