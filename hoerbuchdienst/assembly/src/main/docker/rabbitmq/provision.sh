#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

echo "Checking if RabbitMQ is online"
if rabbitmqctl await_startup; then
  echo "RabbitMQ appears to be online"
else
  echo "RabbitMQ is not online"
  exit 1
fi

federator_password="$(pwgen -BCn 16 1)"
rabbitmqctl change_password federator "${federator_password}"
echo "RabbitMQ federator password is ${federator_password}"

if [[ -z "${RABBITMQ_SHARDS}" ]]; then
  rabbitmq-setup-federation.sh "${RABBITMQ_SHARDS}"
elif [[ $# -gt 0 ]]; then
  rabbitmq-setup-federation.sh "$@"
else
  echo "usage: $0 <server1:user:pwd[ server2:user:pwd .. serverN:user:pwd]>"
  echo "or set environment variable RABBITMQ_SHARDS"
fi

exit 0
