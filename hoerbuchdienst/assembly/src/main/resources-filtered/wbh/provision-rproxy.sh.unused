#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

PROJECT_NAME="${docker.project.name}"

set -o nounset
set -o errexit

# TODO default_server
#echo "Enabling default server"
#docker-compose exec rproxy \
#    mv /etc/nginx/conf.d/default_tls_server.conf.disabled /etc/nginx/conf.d/default_tls_server.conf
#echo "done"
echo "Enabling reverse proxy for Vault"
docker-compose -p ${PROJECT_NAME} exec rproxy \
    mv /etc/nginx/conf.d/vault.conf.disabled /etc/nginx/conf.d/vault.conf
echo "done"
echo "Enabling reverse proxy for MinIO"
docker-compose -p ${PROJECT_NAME} exec rproxy \
    mv /etc/nginx/conf.d/minio.conf.disabled /etc/nginx/conf.d/minio.conf
echo "done"
echo "Enabling reverse proxy for RabbitMQ"
docker-compose -p ${PROJECT_NAME} exec rproxy \
    mv /etc/nginx/conf.d/rabbitmq.conf.disabled /etc/nginx/conf.d/rabbitmq.conf
echo "done"
echo "Enabling reverse proxy for Hoerbuchdienst"
docker-compose -p ${PROJECT_NAME} exec rproxy \
    mv /etc/nginx/conf.d/hoerbuchdienst.conf.disabled /etc/nginx/conf.d/hoerbuchdienst.conf
echo "done"
#docker-compose -p ${PROJECT_NAME} restart rproxy

exit 0
