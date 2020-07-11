#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 2 ]]; then
  echo "usage: $0 <server1:user:pwd[ server2:user:pwd .. serverN:user:pwd]>"
fi

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

rabbitmq-setup-federation.sh "$@"

exit 0
