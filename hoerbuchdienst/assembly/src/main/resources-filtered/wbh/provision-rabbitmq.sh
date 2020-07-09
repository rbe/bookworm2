#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

PROJECT_NAME="${docker.project.name}"
domain_name="${domain}"
tld="${domain_name/*.}"

if [[ $# == 1 ]]; then
    FEDERATOR_PASSWORD="$1"
else
    if [ $(command -v pwgen) ]; then
        FEDERATOR_PASSWORD="$(pwgen -BCn 16 1)"
    else
        FEDERATOR_PASSWORD="$(docker run --rm alpine:latest ash -c "apk add -q pwgen && pwgen -BCn 16 1")"
    fi
    echo "Password is ${FEDERATOR_PASSWORD}"
fi

docker-compose -p ${PROJECT_NAME} exec rabbitmq \
    rabbitmqctl change_password \
    federator "${FEDERATOR_PASSWORD}"
if [[ "${tld}" != "local" ]]; then
    docker-compose -p ${PROJECT_NAME} exec rabbitmq \
        rabbitmq-setup-federation.sh \
        federator:"${FEDERATOR_PASSWORD}"
fi

exit 0
