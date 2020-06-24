#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(
  pushd $(dirname $0) >/dev/null
  pwd
  popd >/dev/null
)"

if [[ $# == 1 ]]; then
  FEDERATOR_PASSWORD=$1
else
  if [ $(command -v pwgen) ]; then
    FEDERATOR_PASSWORD="$(pwgen -BCn 16 1)"
  else
    FEDERATOR_PASSWORD="$(docker run --rm alpine:3.12 ash -c "apk add -q pwgen ; pwgen -BCn 16 1")"
  fi
  echo "Password is ${FEDERATOR_PASSWORD}"
fi

"${execdir}"/provision-rabbitmq.sh "${FEDERATOR_PASSWORD}"
"${execdir}"/provision-minio.sh
"${execdir}"/provision-rproxy.sh

exit 0
