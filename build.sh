#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null ; pwd ; popd >/dev/null)"

hostname="$(hostname -f)"
MAVEN_OPTS="-Ddomain=${hostname}"

pushd "${execdir}" >/dev/null
git reset --hard
git pull
docker run \
    --rm \
    --hostname maven \
    --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
    --mount type=bind,source=${execdir},destination=/var/local \
    -e MAVEN_OPTS="${MAVEN_OPTS}" \
    maven:3.6.3-openjdk-11 \
    bash -c "cd /var/local && mvn clean && mvn compile && mvn package && mvn verify"
popd >/dev/null

exit 0
