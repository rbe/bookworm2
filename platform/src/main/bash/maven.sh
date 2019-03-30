#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

function mvnw_build() {
    local profiles=$1
    ./mvnw -s settings.xml \
        -Dmaven.repo.local=$(pwd)/.mvn/repository \
        -Dmaven.artifact.threads=10 \
        -P ${profiles} \
        -T 4 \
        clean install
}
