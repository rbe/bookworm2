#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 <env>"
  echo "  env        dev | prod"
  exit 1
fi
env=$1
execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"
MAVEN_REPO="${execdir}/../maven-repository"
[[ ! -d ${MAVEN_REPO} ]] && mkdir -p "${MAVEN_REPO}"
MAVEN_REPO="$(
  pushd "${MAVEN_REPO}" >/dev/null
  pwd
  popd >/dev/null
)"
MAVEN_REPO_CNT="/var/local/maven-repository"
MAVEN_OPTS="-Xshare:on -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+UseParallelGC -Dmaven.repo.local=${MAVEN_REPO_CNT} -DlocalRepository=${MAVEN_REPO_CNT} -Dmaven.artifact.threads=10"
MAVEN_CMD_LINE_ARGS="-s .mvn/settings.xml --batch-mode --fail-fast"

ssh-keygen -R github.com
ssh-keyscan github.com 2>/dev/null 1>>~/.ssh/known_hosts
ssh-keygen -R bitbucket.org
ssh-keyscan bitbucket.org 2>/dev/null 1>>~/.ssh/known_hosts
ssh-keygen -H

echo "Building Docker Image 'Java/Maven/Docker builder'"
pushd "${execdir}"/builder/openjdk11-maven-docker >/dev/null
docker build -t wbh-bookworm/builder:1 .
popd >/dev/null
echo "done"

echo "Updating Mikrokosmos"
pushd "${execdir}"/../mikrokosmos >/dev/null
git reset --hard && git pull
echo "done"
echo "Building Mikrokosmos"
rm -rf "${MAVEN_REPO}/aoc/mikrokosmos"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  --mount type=bind,source=$(pwd),destination=/var/local/source \
  -e MAVEN_OPTS="${MAVEN_OPTS}" \
  wbh-bookworm/builder:1 \
  ash -c "cd /var/local/source && rm -f .mvn/maven.config && java -Xshare:dump && mvn ${MAVEN_CMD_LINE_ARGS} clean verify && mvn clean install" |
  tee build-mikrokosmos.bookworm.log
popd >/dev/null
echo "done"

echo "Updating WBH Bookworm"
pushd "${execdir}" >/dev/null
git reset --hard && git pull
echo "done"
HOSTNAME="$(hostname -f)"
echo "Building WBH Bookworm for ${HOSTNAME}"
rm -rf "${MAVEN_REPO}/wbh"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  --mount type=bind,source=$(pwd),destination=/var/local/source \
  -e MAVEN_OPTS="${MAVEN_OPTS} -Ddomain=${HOSTNAME}" \
  wbh-bookworm/builder:1 \
  ash -c "cd /var/local/source && rm -f .mvn/maven.config && java -Xshare:dump && mvn ${MAVEN_CMD_LINE_ARGS} -P bookworm.docker.${env} clean install" |
  tee build-wbh.bookworm.log
popd >/dev/null
echo "done"

exit 0
