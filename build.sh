#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <env> <project>"
  echo "  env        dev | prod"
  echo "  project    cms-hbk | hbd"
fi
env=$1
shift
project=$1

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"

MAVEN_REPO="$(
  pushd "${execdir}/../maven-repository" >/dev/null
  pwd
  popd >/dev/null
)"
MAVEN_REPO_CNT="/var/local/maven-repository"
MAVEN_OPTS="-Xshare:on -XX:TieredStopAtLevel=1 -XX:+UseParallelGC -Dmaven.repo.local=${MAVEN_REPO_CNT} -DlocalRepository=${MAVEN_REPO_CNT} -Dmaven.artifact.threads=10"
MAVEN_CMD_LINE_ARGS="-B -s .mvn/settings.xml --fail-fast -P bookworm.docker.${env}"

case "${project}" in
cms-hbk)
  MAVEN_PL=":wbh.bookworm.cms.assembly,:wbh.bookworm.hoerbuckatalog.deployment"
  ;;
hbd)
  MAVEN_PL=":wbh.bookworm.hoerbuchdienst.application.assembly"
  ;;
esac

echo "Building Docker Image 'builder'"
pushd "${execdir}"/builder/openjdk11-maven-docker >/dev/null
docker build -t wbh-bookworm/builder:1 .
popd >/dev/null
echo "done"

#./mvnw ${mvnp} \
#  org.codehaus.mojo:versions-maven-plugin:2.7:set \
#  -DnewVersion=${VERSION}

echo "Updating Mikrokosmos"
pushd "${execdir}"/../mikrokosmos >/dev/null
git reset --hard
git pull
echo "done"
echo "Building Mikrokosmos"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  --mount type=bind,source=$(pwd),destination=/var/local/source \
  -e MAVEN_OPTS="${MAVEN_OPTS}" \
  -e MAVEN_CMD_LINE_ARGS="${MAVEN_CMD_LINE_ARGS}" \
  wbh-bookworm/builder:1 \
  ash -c "cd /var/local/source && rm .mvn/maven.config && java -Xshare:dump && mvn help:effective-pom clean verify && mvn install" |
  tee build-mikrokosmos.bookworm.log
popd >/dev/null
echo "done"

echo "Updating WBH Bookworm"
pushd "${execdir}" >/dev/null
git reset --hard
git pull
echo "done"
HOSTNAME="$(hostname -f)"
echo "Building WBH Bookworm for ${HOSTNAME}"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  --mount type=bind,source=$(pwd),destination=/var/local/source \
  -e MAVEN_OPTS="${MAVEN_OPTS} -Ddomain=${HOSTNAME}" \
  -e MAVEN_CMD_LINE_ARGS="${MAVEN_CMD_LINE_ARGS}" \
  wbh-bookworm/builder:1 \
  ash -c "cd /var/local/source && rm .mvn/maven.config && java -Xshare:dump && mvn -pl ${MAVEN_PL} help:effective-pom clean verify" |
  tee build-wbh.bookworm.log
popd >/dev/null
echo "done"

exit 0
