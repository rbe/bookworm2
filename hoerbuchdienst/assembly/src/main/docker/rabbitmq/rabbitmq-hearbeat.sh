#!/usr/bin/env bash

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 [<federator password>]"
  exit 1
fi

my_hostname="$(hostname -f)"

function send() {
  timestamp="$(date -u +%Y-%m-%dT%H:%M:%S.000000Z)"
  echo "Heartbeat at ${timestamp}"
  rabbitmqadmin \
    -s --host="rabbitmq.shard1.audiobook.wbh-online.de" --port=15671 \
    --username=federator --password="${FEDERATOR_PASSWORD}" \
    --vhost="${MY_RABBITMQ_VHOST}" \
    publish exchange="federated.heartbeat" routing_key="" \
    payload="{\"pointInTime\":\"${timestamp}\", \"shardname\":\"${my_hostname}#N\"}" \
    properties="{\"headers\":{\"x-hostname\":\"${my_hostname}\"}}"
}

for ((i = 0; i < 100; i++)); do
  send
  [[ $((i % 3)) == 0 ]] && sleep 7
done

exit 0
