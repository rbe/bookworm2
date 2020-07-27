#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 3 ]]; then
  echo "usage: $0 <federator password> <exchange> <payload>"
  exit 1
fi

federator_password="$1"
shift
exchange="$1"
shift
payload="$*"

rabbitmqadmin \
  -s \
  --host="$(hostname -f)" --port=15671 \
  --username=federator --password="${federator_password}" \
  --vhost="${MY_RABBITMQ_VHOST}" \
  publish exchange="${exchange}" routing_key="" payload="${payload}"

exit 0
