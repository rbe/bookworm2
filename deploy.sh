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
releasedir="$(
  pushd "${execdir}/../releases" >/dev/null
  pwd
  popd >/dev/null
)"
COMPOSE_PROJECT="${env}-${project}"
ARTIFACT="${env}-${project}-${timestamp}"

echo "Deploying WBH Bookworm ${env}"
if [[ ! -d "${releasedir}" ]]; then
  mkdir "${releasedir}"
fi
# check releases directory for artifact
if [[ ! -f "${releasedir}"/${ARTIFACT} ]]; then
  unzip "${assemblydir}/${ARTIFACT}"-LocalBuild.zip \
    docker-compose.yml docker-compose.${env}.yml \
    -d "${releasedir}/${ARTIFACT}"
else
  echo "Artifact ${ARTIFACT} already exists"
fi

pushd "${releasedir}/${ARTIFACT}" >/dev/null
echo "Starting ${env} containers from $(pwd)"
docker-compose \
  -p "${COMPOSE_PROJECT}" \
  -f docker-compose.yml -f docker-compose.${env}.yml \
  up \
  -d \
  --no-build
popd >/dev/null
echo "done"

exit 0
