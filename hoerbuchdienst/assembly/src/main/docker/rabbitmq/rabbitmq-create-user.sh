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

username="$(pwgen -Bcn 16 1)"
password="$(pwgen -BCn 32 1)"
echo "Generated RabbitMQ ${username} with password ${password}"
if rabbitmqctl change_password "${username}" "${password}"; then
  echo "done"
else
  echo "failed"
fi

exit 0
