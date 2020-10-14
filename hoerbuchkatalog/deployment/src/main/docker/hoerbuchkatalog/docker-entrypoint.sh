#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

umask 0007

TZ="Europe/Berlin"
export TZ

ls -l /usr/local/service.jar
ls -la /var/local

rm -rf /var/local/wbh/hoerbuchkatalog/lucene/*

java \
  -Xms2048m -Xmx2048m \
  -XX:+UseCompressedOops \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/local/java_debug \
  -XX:ErrorFile=/var/local/java_debug/java_error_%p.log \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+UseZGC \
  -Xlog:gc=info,gc+stats:file=/var/local/java_debug/gc.log:time,uptime,pid:filecount=16,filesize=128M \
  -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n \
  -Dlogback.configurationFile=/var/local/logback.xml \
  -jar /usr/local/service.jar \
  --spring.profiles.active=production \
  --spring.config.additional-location=/var/local/application-secrets.yml

exit 0
