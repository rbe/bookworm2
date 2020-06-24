#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

hostname="$(hostname -f)"
MAVEN_OPTS="-Ddomain=${hostname}"

docker run \
  --rm \
  --hostname maven \
  --mount type=bind,source=/var/run/docker.sock,destination=/var/run/docker.sock \
  --mount type=bind,source=$(pwd),destination=/var/local \
  -e MAVEN_OPTS="${MAVEN_OPTS}" \
  maven:3.6.3-openjdk-11 \
  bash -c "cd /var/local && mvn clean && mvn compile && mvn package && mvn verify"

exit 0
