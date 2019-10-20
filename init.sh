#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

PROJECT=~/project/wbh.bookworm
REPO=artofcoding/bookworm2.git
BRANCH=develop

#
# DO NOT MODIFY LINES BELOW
#

set -o nounset
set -o errexit

git clone -b ${BRANCH} git@bitbucket.org:${REPO} ${PROJECT}
cd ${PROJECT}
./mvnw clean

exit 0
