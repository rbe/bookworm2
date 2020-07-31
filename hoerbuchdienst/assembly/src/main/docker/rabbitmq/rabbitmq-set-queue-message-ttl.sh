#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 3 ]]; then
  echo "usage: $0 <policy name> <queue pattern> <TTL in seconds>"
  exit 1
fi

echo "Checking if RabbitMQ is online"
if rabbitmqctl await_startup; then
  echo "RabbitMQ appears to be online"
else
  echo "RabbitMQ is not online"
  exit 1
fi

policy_name="$1"
shift
queue_pattern="$1"
shift
ttl="$1"

#rabbitmqctl set_operator_policy TTL ".*" '{"message-ttl":60000}' --apply-to queues
rabbitmqctl set_policy --vhost="${MY_RABBITMQ_VHOST}" \
  "${policy_name}" "${queue_pattern}" "{\"message-ttl\":${ttl}}" \
  --priority 1 --apply-to queues

exit 0
