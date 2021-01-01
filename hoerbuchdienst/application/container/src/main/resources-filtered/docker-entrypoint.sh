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
JAVA_TOOL_OPTIONS="-Xms8192m -Xmx8192m \
-XX:MaxDirectMemorySize=16384m \
-Djdk.nio.maxCachedBufferSize=262144 \
-XX:+UseCompressedOops \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/var/local/java_debug \
-XX:ErrorFile=/var/local/java_debug/java_error_%p.log \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseZGC"
if [[ -f "/var/local/.java_debug" ]]; then
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} \
-Xlog:gc=info,gc+stats:file=/var/local/java_debug/gc.log:time,uptime,pid:filecount=16,filesize=16M \
-Xrunjdwp:transport=dt_socket,address=*:5005,server=y,suspend=n \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=1099 \
-Dcom.sun.management.jmxremote.rmi.port=1099 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Djava.rmi.server.hostname=\$(hostname -f)"
fi
export JAVA_TOOL_OPTIONS

java \
  -Dmicronaut.environments=prod \
  -Dmicronaut.config.files=/var/local/application-shard.yml \
  -jar /usr/local/service.jar

#if [ -x "$(which jstatd)" ]; then
#  cat >/var/local/jstatd.policy <<EOF
#grant codebase "jrt:/jdk.jstatd" {
# permission java.security.AllPermission;
#};
#grant codebase "jrt:/jdk.internal.jvmstat" {
# permission java.security.AllPermission;
#};
#EOF
#  echo "Starting jstatd"
#  jstatd \
#    -p 1099 \
#    -J-Djava.security.policy=/var/local/jstatd.policy \
#    -J-Djava.rmi.server.hostname=${hbd.hostname} \
#    -J-Djava.rmi.server.logCalls=true \
#    -n jstatdserver |
#    tee 1>/var/local/jstatd.log 2>&1
#fi

tail -f /dev/null

exit 0
