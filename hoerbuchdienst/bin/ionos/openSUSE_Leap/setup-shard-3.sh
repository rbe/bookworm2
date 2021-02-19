#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

pushd ~/bookworm2 >/dev/null
git fetch origin
git checkout origin/master -- autoupdate.sh
autoupdate.sh prod hbd force
popd >/dev/null

rm /var/lib/docker/volumes/prod-hbd_rproxycerts/_data/is_initialized
cnt.sh prod hbd exec hbd-rproxy provision.sh default_tls_server
cnt.sh prod hbd restart hbd-rproxy
cnt.sh prod hbd exec hbd-rproxy provision.sh default_tls_server portainer minio rabbitmq

cnt.sh prod hbd exec rabbitmq rabbitmq-provision.sh
cnt.sh prod hbd exec rabbitmq rabbitmq-change-password.sh bugs

# MQ verknüpfen

# Hörbuchdienst konfigurieren
bugs_pwd=""
minio_access_key=""
minio_secret_key=""
cat >/var/lib/docker/volumes/prod-hbd_hoerbuchdienstlocal/_data/application-shard.yml <<EOF
shard:
  name: $(hostname -s)

micronaut:
  server:
    max-request-size: 1mb
    multipart:
      max-file-size: 1mb

hoerbuchdienst:
  objectstorage:
    path: /var/local

rabbitmq:
  servers:
    default:
      uri: amqps://bugs:${bugs_pwd}@rabbitmq-\${shard.name}.wbh-online.de/hoerbuchdienst

objectstorage:
  minio:
    url: http://minio:9000
    access_key: ${minio_access_key}
    secure_key: ${minio_secret_key}
EOF

cnt.sh prod hbd restart hoerbuchdienst
cnt.sh prod hbd exec hbd-rproxy provision.sh hoerbuchdienst

exit 0
