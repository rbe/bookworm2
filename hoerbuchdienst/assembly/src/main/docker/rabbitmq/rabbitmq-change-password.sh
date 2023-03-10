#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <username> [<password>]"
  exit 1
fi

echo "Checking if RabbitMQ is online"
if rabbitmqctl await_startup; then
  echo "RabbitMQ appears to be online"
else
  echo "RabbitMQ is not online"
  exit 1
fi

username="$1"
echo "Setting password for RabbitMQ user ${username}"
if [[ $# == 2 ]]; then
  password="$1"
else
  password="$(pwgen -BCn 32 1)"
  echo "Generated RabbitMQ ${username} password: ${password}"
fi
if rabbitmqctl change_password "${username}" "${password}"; then
  echo "done"
else
  echo "failed"
fi

exit 0
