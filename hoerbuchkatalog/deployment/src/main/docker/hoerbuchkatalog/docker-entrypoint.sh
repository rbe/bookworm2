#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

umask 0007
TZ="Europe/Berlin"
export TZ

cd /opt/bookworm
rm -rf var/wbh/hoerbuchkatalog/lucene/*
SPRING_APPLICATION_JSON="$(cat conf/secrets.json)" \
  java \
  -Xms2g -Xmx2g \
  -jar app/wbh.bookworm.hoerbuchkatalog.assembly.jar \
  --spring.profiles.active=production \
  --spring.config.additional-location=conf/

exit 0
