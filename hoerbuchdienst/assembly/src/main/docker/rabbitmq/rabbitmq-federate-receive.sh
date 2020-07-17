#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <federator password> <queue>"
  exit 1
fi

federator_password="$1"
shift
queue="$1"

rabbitmqadmin \
  -s \
  --host="$(hostname -f)" --port=15671 \
  --username=federator --password="${federator_password}" \
  --vhost="${MY_RABBITMQ_VHOST}" \
  get queue="${queue}" ackmode=ack_requeue_false

exit 0
