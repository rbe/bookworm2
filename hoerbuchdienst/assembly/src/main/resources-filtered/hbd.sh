#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(
  pushd   "$(dirname "$0")" >/dev/null
  pwd
  popd   >/dev/null
)"

usage() {
  echo   "usage: $0 <destroy | provision | start | stop> <env> <project>"
  echo   "    env        dev | prod"
  echo   "    project    hbk | hbd"
  exit   1
}

[[ $# != 3 ]] && usage

mode=$1
shift
env=$1
shift
project="$1"
shift
dc="docker-compose -p ${project} -f docker-compose.yml -f docker-compose.${env}.yml"
case "${mode}" in
  destroy)
    [[ $# -eq 1     ]] && all="$1" || all="no"
    pushd     "${execdir}" >/dev/null
    docker-compose     -p "${project}" down
    set     +o errexit
    docker     system prune -f
    # TODO Daten nicht zerstören! Vorher: Master Key aus Vault sichern!
    [[ "${all}" == "all"     ]] && docker volume rm "${env}-${project}_miniodata"
    [[ "${all}" == "all"     ]] && docker volume rm "${env}-${project}_miniolocal"
    docker     volume rm "${project}_mcrootconfig"
    docker     volume rm "${project}_mclocal"
    [[ "${all}" == "all"     ]] && docker volume rm "${env}-${project}_keslocal"
    [[ "${all}" == "all"     ]] && docker volume rm "${env}-${project}_vaultconfig"
    [[ "${all}" == "all"     ]] && docker volume rm "${env}-${project}_vaultfile"
    [[ "${all}" == "all"     ]] && docker volume rm "${env}-${project}_vaultlogs"
    docker     volume rm "${project}_rabbitmqconf"
    docker     volume rm "${project}_rabbitmqdata"
    docker     volume rm "${project}_virenscannerdata"
    [[ "${all}" == "all"     ]] && docker volume rm storage_rproxycerts
    docker     volume rm "${project}"_rproxyconf
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/rproxy:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/vault:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/kes:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/minio:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/mc:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/rabbitmq:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/virenscanner:${HBD_RELEASE}"
    [[ "${all}" == "all"     ]] && docker image rm "wbh-${project}/hoerbuchdienst:${HBD_RELEASE}"
    docker     image prune -f
    set     -o errexit
    popd     >/dev/null
    ;;
  provision)
    pushd     "${execdir}" >/dev/null
    echo     "Starting nginx"
    ${dc}     up -d hbd-rproxy
    if     [[ ! -f /var/lib/docker/volumes/storage_rproxycerts/_data/is_initialized ]]; then
      domain_name="${domain}"
      tld="${domain_name/*./}"
      [[ "${tld}" == "local"     ]] && wait_for_nginx=5 || wait_for_nginx=60
      echo     "Waiting ${wait_for_nginx} seconds for TLS certificate generation"
      sleep     ${wait_for_nginx}
    fi
    echo     "done"
    echo     "Starting Vault"
    ${dc}     up -d vault
    sleep     5
    echo     "done"
    echo     "Starting MinIO KES"
    ${dc}     up -d kes
    sleep     5
    echo     "done"
    echo     "Starting MinIO to exchange keys"
    ${dc}     up -d minio
    sleep     10
    echo     "done"
    echo     "Removing MinIO _OLD keys"
    rm     -f "${execdir}/.env"
    #sed -i'' \
    #    -e "/MINIO_ACCESS_KEY_OLD=\(.*\)/d" \
    #    "${execdir}/docker-compose*.yml"
    #sed -i'' \
    #    -e "/MINIO_SECRET_KEY_OLD=\(.*\)/d" \
    #    "${execdir}/docker-compose*.yml"
    echo     "done"
    echo     "Enabling MinIO KMS auto encryption"
    sed     -i'' \
      -e       "s#MINIO_KMS_AUTO_ENCRYPTION=.*#MINIO_KMS_AUTO_ENCRYPTION=on#" \
      "${execdir}/docker-compose.yml"
    echo     "done"
    echo     "Waiting for services to come up"
    ${dc}     up -d
    sleep     10
    echo     "done"
    echo     "Setting up shard (RabbitMQ, MinIO, reverse proxy)"
    chmod     +x wbh/*.sh
    wbh/provision-rabbitmq.sh
    wbh/provision-minio.sh
    wbh/provision-rproxy.sh     "${project}" minio hoerbuchdienst
    echo     "done"
    echo     "Stopping all containers"
    ${dc}     down
    echo     "done"
    ;;
  start)
    pushd     "${execdir}" >/dev/null
    ${dc}     up -d
    popd     >/dev/null
    ;;
  stop)
    pushd     "${execdir}" >/dev/null
    ${dc}     down
    popd     >/dev/null
    ;;
  *)
    usage
    ;;
esac

exit 0
