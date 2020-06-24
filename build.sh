#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"

hostname="$(hostname -f)"
MAVEN_OPTS="-Dmaven.repo.local=/var/local/.m2 -Dmaven.artifact.threads=1"

pushd "${execdir}"/mikrokosmos >/dev/null
git reset --hard
git pull
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${execdir},destination=/var/local/mikrokosmos \
  --mount type=bind,source=${execdir}/.m2,destination=/var/local/.m2 \
  maven:3.6.3-openjdk-11 \
  bash -c "cd /var/local/mikrokosmos && mvn clean && mvn compile && mvn package && mvn verify"
popd >/dev/null

pushd "${execdir}" >/dev/null
git reset --hard
git pull
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${execdir},destination=/var/local/bookworm2 \
  --mount type=bind,source=${execdir}/.m2,destination=/var/local/.m2 \
  -e MAVEN_OPTS="${MAVEN_OPTS} -Ddomain=${hostname}" \
  maven:3.6.3-openjdk-11 \
  bash -c "cd /var/local/bookworm2 && mvn clean && mvn compile && mvn package && mvn verify"
popd >/dev/null

exit 0
