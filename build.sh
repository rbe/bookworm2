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
MAVEN_REPO="${execdir}/../.m2"
MAVEN_REPO_CNT="/var/local/.m2"
MAVEN_OPTS="-B -s settings.xml -ff -Dmaven.repo.local=${MAVEN_REPO_CNT} -Dmaven.artifact.threads=10"

echo "Creating local Maven repository destination ${MAVEN_REPO}"
mkdir -p "${MAVEN_REPO}"
echo "done"

echo "Updating Mikrokosmos"
pushd "${execdir}"/../mikrokosmos >/dev/null
git reset --hard
git pull
rm .mvn/maven.config
echo "done"
echo "Building Mikrokosmos"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${execdir}/../mikrokosmos,destination=/var/local/source \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  maven:3.6.3-openjdk-11 \
  bash -c "cd /var/local/source && mvn clean && mvn compile && mvn package && mvn verify"
popd >/dev/null
echo "done"

echo "Updating WBH Bookworm"
pushd "${execdir}" >/dev/null
git reset --hard
git pull
rm .mvn/maven.config
echo "done"
echo "Building WBH Bookworm"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${execdir},destination=/var/local/source \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  -e MAVEN_OPTS="${MAVEN_OPTS} -Ddomain=${hostname}" \
  maven:3.6.3-openjdk-11 \
  bash -c "cd /var/local/source && mvn clean && mvn compile && mvn package && mvn verify"
popd >/dev/null
echo "done"

exit 0
