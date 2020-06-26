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
  exit 1
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
MAVEN_OPTS="-Dmaven.repo.local=${MAVEN_REPO_CNT} -DlocalRepository=${MAVEN_REPO_CNT}"
case "${project}" in
cms-hbk)
  MAVEN_PL="wbh.bookworm.cms.assembly,wbh.bookworm.hoerbuckatalog.deployment"
  ;;
hbd)
  MAVEN_PL="wbh.bookworm.hoerbuckdienst.application.assembly"
  ;;
esac
MAVEN_CMD_LINE_ARGS="-B -s .mvn/settings.xml --fail-fast -P bookworm.docker.${env} -pl ${MAVEN_PL}"

HOSTNAME="$(hostname -f)"
echo "Deploying WBH Bookworm ${env}/${MAVEN_PL} at ${HOSTNAME}"
pushd "${execdir}" >/dev/null
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  --mount type=bind,source=$(pwd),destination=/var/local/source \
  -e MAVEN_OPTS="${MAVEN_OPTS} -Ddomain=${HOSTNAME}" \
  wbh-bookworm/builder:1 \
  ash -c "cd /var/local/source && rm -f .mvn/maven.config && mvn ${MAVEN_CMD_LINE_ARGS} install" |
  tee deploy-wbh.bookworm.log
popd >/dev/null
echo "done"

exit 0
