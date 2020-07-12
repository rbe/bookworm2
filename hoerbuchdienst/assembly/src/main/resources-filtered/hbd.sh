#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"

usage() {
  echo "usage: $0 <destroy | provision | start | stop>"
  exit 1
}

[[ $# != 3 ]] && usage

function wait_for_container {
  local cnt="$1"
  local timeout="$2"
  while ! docker ps | grep -c "${cnt}" >/dev/null; do
    echo "Waiting ${timeout} seconds maximum for container ${cnt}"
    sleep 1
    timeout=$((timeout - 1))
    if [[ ${timeout} == 0 ]]; then
      echo "Timeout of ${timeout} seconds reached waiting for container ${cnt}"
      break
    fi
  done
}

mode="$1"
shift
env="${docker.project.env}"
project="${docker.project.name}"

domain_name="${domain}"
tld="${domain_name/*./}"
dc="docker-compose -p ${env}-${project} -f docker-compose.yml -f docker-compose.${env}.yml"
case "${mode}" in
  destroy)
    [[ $# -eq 1 ]] && all="$1" || all="no"
    pushd "${execdir}" >/dev/null
    docker-compose -p "${project}" down
    set +o errexit
    docker system prune -f
    if [[ "${all}" == "all" ]]; then
      docker volume ls |
        grep hbd |
        awk '{print $2}' |
        xargs docker volume rm
    else
      docker volume ls |
        grep hbd |
        grep -vE "(rproxycerts|vault|kes|minio)" |
        awk '{print $2}' |
        xargs docker volume rm
    fi
    docker image ls |
      grep wbh- |
      grep "${HBD_RELEASE}" |
      awk '{print $1":"$2}' |
      xargs docker image rm
    set -o errexit
    popd >/dev/null
    ;;
  provision)
    pushd "${execdir}" >/dev/null
    echo "Starting nginx"
    ${dc} up -d hbd-rproxy
    [[ "${tld}" == "local" ]] && wait_for_nginx=5 || wait_for_nginx=60
    echo "Waiting ${wait_for_nginx} seconds for TLS certificate generation"
    sleep ${wait_for_nginx}
    ${dc} stop hbd-rproxy
    echo "done"
    echo "Starting Vault"
    ${dc} up -d vault
    sleep 5
    echo "done"
    echo "Starting MinIO KES"
    ${dc} up -d kes
    sleep 5
    echo "done"
    echo "Starting MinIO to exchange keys"
    ${dc} up -d minio
    sleep 10
    echo "done"
    echo "Starting all services"
    ${dc} up -d
    echo "done"
    echo "Setting up shard: MinIO"
    ${dc} exec mc provision.sh
    echo "Setting up shard: reverse proxy"
    ${dc} exec hbd-rproxy provision.sh minio rabbitmq hoerbuchdienst
    echo "done"
    echo "!!! ATTENTION"
    echo "!!! ATTENTION: Don't forget to provision RabbitMQ"
    echo "!!! ATTENTION"
    echo "done"
    echo "Stopping all containers"
    ${dc} down
    echo "done"
    echo "Removing MinIO _OLD keys"
    rm -f "${execdir}/.env"
    #sed -i'' \
    #    -e "/MINIO_ACCESS_KEY_OLD=\(.*\)/d" \
    #    "${execdir}/docker-compose*.yml"
    #sed -i'' \
    #    -e "/MINIO_SECRET_KEY_OLD=\(.*\)/d" \
    #    "${execdir}/docker-compose*.yml"
    echo "done"
    echo "Enabling MinIO KMS auto encryption"
    sed -i'' \
      -e "s#MINIO_KMS_AUTO_ENCRYPTION=.*#MINIO_KMS_AUTO_ENCRYPTION=on#" \
      "${execdir}/docker-compose.yml"
    echo "done"
    ;;
  start)
    pushd "${execdir}" >/dev/null
    ${dc} up -d
    popd >/dev/null
    ;;
  stop)
    pushd "${execdir}" >/dev/null
    ${dc} down
    popd >/dev/null
    ;;
  *)
    usage
    ;;
esac

exit 0
