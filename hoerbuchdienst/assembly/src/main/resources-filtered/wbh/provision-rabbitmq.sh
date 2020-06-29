#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

PROJECT_NAME="${docker.project.name}"

set -o nounset
set -o errexit

if [[ $# == 1 ]]; then
    FEDERATOR_PASSWORD="$1"
else
    if [ $(command -v pwgen) ]; then
        FEDERATOR_PASSWORD="$(pwgen -BCn 16 1)"
    else
        FEDERATOR_PASSWORD="$(docker run --rm alpine:3.12 ash -c "apk add -q pwgen && pwgen -BCn 16 1")"
    fi
    echo "Password is ${FEDERATOR_PASSWORD}"
fi

echo "$0: executing in $(pwd)"

docker-compose -p ${PROJECT_NAME} exec rabbitmq \
    rabbitmqctl change_password \
    federator "${FEDERATOR_PASSWORD}"
docker-compose -p ${PROJECT_NAME} exec rabbitmq \
    rabbitmq-setup-federation.sh \
    federator:"${FEDERATOR_PASSWORD}"

## node1: send message
#docker-compose exec rabbitmq rabbitmqadmin \
#     -s --host="rabbitmq.shard1.audiobook.wbh-online.de" --port=15671 \
#     --username=federator --password="${FEDERATOR_PASSWORD}" \
#     --vhost="${MY_RABBITMQ_VHOST}" \
#     publish exchange="federated.heartbeat" routing_key="" payload="hello, world"
## node2: receive message
#docker-compose exec rabbitmq rabbitmqadmin \
#     -s --host="rabbitmq.shard2.audiobook.wbh-online.de" --port=15671 \
#     --username=federator --password="${FEDERATOR_PASSWORD}" \
#     --vhost="${MY_RABBITMQ_VHOST}" \
#     get queue="heartbeat" ackmode=ack_requeue_false

exit 0
