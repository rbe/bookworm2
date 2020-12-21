#!/usr/bin/env bash
#
# Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

umask 0007

TZ="Europe/Berlin"
export TZ

echo "Starting service"
export MICRONAUT_CONFIG_FILES=""
java \
  -Xms8192m -Xmx8192m \
  -XX:+UseCompressedOops \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/local/java_debug \
  -XX:ErrorFile=/var/local/java_debug/java_error_%p.log \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+UseZGC \
  -Xlog:gc=info,gc+stats:file=/var/local/java_debug/gc.log:time,uptime,pid:filecount=16,filesize=128M \
  -Xrunjdwp:transport=dt_socket,address=*:5005,server=y,suspend=n \
  -Dlogback.configurationFile=/var/local/logback.xml \
  -Dmicronaut.environments=prod \
  -Dmicronaut.config.files=/var/local/application-shard.yml \
  -jar /usr/local/service.jar

if [ -x jstatd ]; then
  echo "Starting jstatd"
  jstatd \
    -J-Djava.security.policy=all.policy \
    -J-Djava.rmi.server.logCalls=true \
    -p 8001 \
    -n jstatdserver
fi

tail -f /dev/null

exit 0
