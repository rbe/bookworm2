#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

PROJECT_NAME="${docker.project.name}"

set -o nounset
set -o errexit

if [[ $# != 1 ]]
then
    echo "usage: $0 [<federator password>]"
    exit 1
fi
FEDERATOR_PASSWORD=$1

execdir="$(pushd $(dirname $0) >/dev/null ; pwd ; popd >/dev/null)"
pushd "${execdir}"/.. >/dev/null
docker-compose -p ${PROJECT_NAME} exec rabbitmq \
    rabbitmqctl change_password \
    federator "${FEDERATOR_PASSWORD}"
docker-compose -p ${PROJECT_NAME} exec rabbitmq \
    rabbitmq-setup-federation.sh \
    federator:"${FEDERATOR_PASSWORD}"
popd >/dev/null

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
