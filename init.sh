#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

PROJECT=~/project/wbh.bookworm
REPO=artofcoding/bookworm2.git
BRANCH=develop

sudo pacman --noconfirm -S git jre-openjdk

git clone -b ${BRANCH} git@bitbucket.org:${REPO} ${PROJECT}
cd ${PROJECT}
./mvnw clean

exit 0
