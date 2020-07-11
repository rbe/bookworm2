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
    echo "usage: $0 < destroy | provision | start | stop > <env> <project>"
    exit 1
}

[[ $# != 3 ]] && usage

mode=$1
shift
env=$1
shift
PROJECT_NAME="$1"
shift
dc="docker-compose -p "${PROJECT_NAME}" -f docker-compose.yml -f docker-compose.${env}.yml"
case "${mode}" in
    destroy)
        [[ $# -eq 1 ]] && all="$1" || all="no"
        pushd "${execdir}" >/dev/null
        docker-compose -p "${PROJECT_NAME}" down
        set +o errexit
        docker system prune -f
        # TODO Daten nicht zerstören! Vorher: Master Key aus Vault sichern!
        docker volume rm "${PROJECT_NAME}"_miniodata
        docker volume rm "${PROJECT_NAME}"_miniolocal
        docker volume rm "${PROJECT_NAME}"_mcrootconfig
        docker volume rm "${PROJECT_NAME}"_mclocal
        docker volume rm "${PROJECT_NAME}"_keslocal
        docker volume rm "${PROJECT_NAME}"_vaultconfig
        docker volume rm "${PROJECT_NAME}"_vaultfile
        docker volume rm "${PROJECT_NAME}"_vaultlogs
        docker volume rm "${PROJECT_NAME}"_rabbitmqconf
        docker volume rm "${PROJECT_NAME}"_rabbitmqdata
        [[ "${all}" == "all" ]] && docker volume rm storage_rproxycerts
        docker volume rm "${PROJECT_NAME}"_rproxyconf
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/rproxy:"${NGINX_RELEASE}"
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/vault:"${VAULT_RELEASE}"
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/kes:"${KES_RELEASE}"
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/minio:"${MINIO_RELEASE}"
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/mc:"${MC_RELEASE}"
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/rabbitmq:"${RABBITMQ_RELEASE}"
        [[ "${all}" == "all" ]] && docker image rm "${PROJECT_NAME}"/hoerbuchdienst:"${HBD_RELEASE}"
        docker image prune -f
        set -o errexit
        popd >/dev/null
        ;;
    provision)
        pushd "${execdir}" >/dev/null
        if [[ ! -f /var/lib/docker/volumes/storage_rproxycerts/_data/is_initialized ]]; then
            echo "Starting nginx to generate TLS server certitifaces"
            ${dc} up -d hbd-rproxy
            domain_name="${domain}"
            tld="${domain_name/*./}"
            [[ "${tld}" == "local" ]] && wait_for_nginx=5 || wait_for_nginx=60
            echo "Waiting ${wait_for_nginx} seconds"
            sleep ${wait_for_nginx}
            echo "done"
        fi
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
        echo "Waiting for services to come up"
        ${dc} up -d
        sleep 10
        echo "done"
        echo "Setting up shard"
        chmod +x wbh/*.sh
        wbh/provision-rabbitmq.sh
        wbh/provision-minio.sh
        wbh/provision-rproxy.sh minio hoerbuchdienst
        echo "done"
        echo "Stopping all containers"
        ${dc} down
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
