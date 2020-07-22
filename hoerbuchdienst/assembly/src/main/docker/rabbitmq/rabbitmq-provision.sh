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

echo "Setting password for RabbitMQ federator"
if [[ $# == 1 ]]; then
  federator_password="$1"
else
  federator_password="$(pwgen -BCn 16 1)"
  echo "Generated RabbitMQ federator password: ${federator_password}"
fi
if rabbitmqctl change_password federator "${federator_password}"; then
  echo "done"
else
  echo "failed"
fi

rabbitmq-export.sh

exit 0
