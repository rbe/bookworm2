#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

PROJECT_NAME="${docker.project.name}"

set -o nounset
set -o errexit

execdir="$(pushd $(dirname $0) >/dev/null ; pwd ; popd >/dev/null)"

usage() {
    echo "usage: $0 < destroy | provision | start | stop >"
    exit 1
}

[[ $# -lt 1 ]] && usage

mode=$1 ; shift
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
        if [[ ! -f /var/lib/docker/volumes/storage_rproxycerts/_data/is_initialized ]]
        then
            echo "Starting nginx to generate TLS server certitifaces"
            docker-compose -p "${PROJECT_NAME}" up -d rproxy
            echo "Waiting 60 seconds"
            sleep 60
            echo "done"
        fi
        echo "Starting Vault"
        docker-compose -p "${PROJECT_NAME}" up -d vault
        sleep 5
        echo "done"
        echo "Starting MinIO KES"
        docker-compose -p "${PROJECT_NAME}" up -d kes
        sleep 5
        echo "done"
        echo "Starting MinIO to exchange keys"
        docker-compose -p "${PROJECT_NAME}" up -d minio
        sleep 10
        echo "done"
        echo "Stopping all containers"
        docker-compose -p "${PROJECT_NAME}" down
        echo "done"
        echo "Removing MinIO _OLD keys"
        sed -i'' \
            -e "/MINIO_ACCESS_KEY_OLD=\(.*\)/d" \
            "${execdir}"/variables-minio.env
        sed -i'' \
            -e "/MINIO_SECRET_KEY_OLD=\(.*\)/d" \
            "${execdir}"/variables-minio.env
        echo "done"
        echo "Enabling MinIO KMS auto encryption"
        sed -i'' \
            -e "s#MINIO_KMS_AUTO_ENCRYPTION=.*#MINIO_KMS_AUTO_ENCRYPTION=on#" \
            "${execdir}"/variables-minio.env
        echo "done"
        echo "Waiting for services to come up"
        docker-compose -p "${PROJECT_NAME}" up -d
        sleep 10
        echo "done"
        echo "Setting up shard"
        chmod +x wbh/*.sh
        wbh/setup-shard.sh
        echo "done"
        echo "Shutting down all services"
        docker-compose -p "${PROJECT_NAME}" down
        sleep 10
        echo "done"
    ;;
    start)
        pushd "${execdir}" >/dev/null
        docker-compose -p "${PROJECT_NAME}" up -d
        popd >/dev/null
    ;;
    stop)
        pushd "${execdir}" >/dev/null
        docker-compose -p "${PROJECT_NAME}" down
        popd >/dev/null
    ;;
    *)
        usage
    ;;
esac

exit 0
