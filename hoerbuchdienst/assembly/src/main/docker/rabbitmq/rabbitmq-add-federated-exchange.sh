#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

my_name="$(hostname -f)"

if [[ $# != 5 ]]; then
  echo "usage: $0 <credentials> <exchange name> <exchange type> <queue name> <queue durable?>"
  echo "  credentials      user:password for RabbitMQ administration"
  echo "  exchange name    name of exchange without prefix for federation (${MY_RABBITMQ_FEDERATION_PREFIX})"
  echo "  exchange type    direct | fanout | topic"
  echo "  queue durable?   true | false"
  exit 1
fi

credentials="$1"
shift
username=${credentials/:*/}
password=${credentials/*:/}
exchange_name="${MY_RABBITMQ_FEDERATION_PREFIX}$1"
shift
exchange_type="$1"
shift
queue_name="$1"
shift
queue_durable="$1"
shift

echo "Checking if RabbitMQ is online"
if rabbitmqctl await_startup; then
  echo "RabbitMQ appears to be online"
else
  echo "RabbitMQ is not online"
  exit 1
fi

echo "Declaring exchange ${exchange_name}"
rabbitmqadmin -s --host "${my_name}" --port 15671 \
  --username="${username}" --password="${password}" \
  --vhost="${MY_RABBITMQ_VHOST}" \
  declare exchange name="${exchange_name}" type="${exchange_type}"
echo "done"

echo "Declaring queue ${queue_name}"
rabbitmqadmin -s --host "${my_name}" --port 15671 \
  --username="${username}" --password="${password}" \
  --vhost="${MY_RABBITMQ_VHOST}" \
  declare queue name="${queue_name}" durable="${queue_durable}"
echo "done"

echo "Creating binding between exchange ${exchange_name} and ${queue_name}"
rabbitmqadmin -s --host "${my_name}" --port 15671 \
  --username="${username}" --password="${password}" \
  --vhost="${MY_RABBITMQ_VHOST}" \
  declare binding source="${exchange_name}" destination_type="queue" destination="${queue_name}" routing_key=""
echo "done"

exit 0
