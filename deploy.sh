#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

env="${1:-prod}" ; shift
echo "Starting WBH Bookworm: ${env}"
docker run \
  --rm \
  --name maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=${MAVEN_REPO},destination=${MAVEN_REPO_CNT} \
  --mount type=bind,source=$(pwd),destination=/var/local/source \
  -e MAVEN_OPTS="${MAVEN_OPTS}" \
  -e MAVEN_CMD_LINE_ARGS="${MAVEN_CMD_LINE_ARGS}" \
  wbh-bookworm/builder:1 \
  ash -c "cd /var/local/source && mvn -P bookworm.docker.${env} deploy" \
  | tee deploy-wbh.bookworm.log
popd >/dev/null
echo "done"

exit 0
