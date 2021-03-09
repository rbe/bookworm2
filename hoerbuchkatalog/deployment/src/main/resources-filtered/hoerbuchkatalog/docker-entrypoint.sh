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

echo "Starting cron"
sudo crond -b -l 0 -d 0 -L /proc/self/fd/1

echo "Removing all/old Lucene index directories"
rm -rf /var/local/wbh/hoerbuchkatalog/lucene/*

echo "Starting service"
JAVA_TOOL_OPTIONS="-Xms2048m -Xmx2048m \
-XX:MaxDirectMemorySize=32m \
-Djdk.nio.maxCachedBufferSize=262144 \
-XX:+UseCompressedOops \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/var/local/java_debug \
-XX:ErrorFile=/var/local/java_debug/java_error_%p.log \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseZGC \
-Dspring.main.banner-mode=off \
-Dspring.output.ansi.enabled=never"
if [[ -f "/var/local/.java_debug" ]]; then
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} \
-Xlog:gc=info,gc+stats:file=/var/local/java_debug/gc.log:time,uptime,pid:filecount=16,filesize=16M \
-Xrunjdwp:transport=dt_socket,address=*:5005,server=y,suspend=n \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=1099 \
-Dcom.sun.management.jmxremote.rmi.port=1099 \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Djava.rmi.server.hostname=$(hostname -f) \
-Dspring.jmx.enabled=true \
-Dspring.liveBeansView.mbeanDomain \
-Dspring.application.admin.enabled=true"
fi
export JAVA_TOOL_OPTIONS

SPRING_PROFILE=prod
if hostname -f | grep -c beta; then
    SPRING_PROFILE=beta
fi

java -jar /usr/local/service.jar \
  --spring.profiles.active=${SPRING_PROFILE} \
  --spring.config.additional-location=/var/local/application-secrets.yml

#if [ -x jstatd ]; then
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
#    -J-Djava.rmi.server.hostname=${hbk.hostname} \
#    -J-Djava.rmi.server.logCalls=true \
#    -n jstatdserver
#fi

exit 0
