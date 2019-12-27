#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

#
# DO NOT MODIFY LINES BELOW
#

set -o nounset
set -o errexit

if [[ $(basename $0) == dc.sh ]]
then
    echo "Please do not call this script directly."
    echo "Use a link:"
    echo "  ln -s dc.sh <envname>.sh and create etc/<envname>.env"
    exit 1
fi

if [[ $# -lt 1 ]]
then
    echo "usage: $0 { assembly }"
    echo "usage: $0 { up | start | stop | restart | down | ps | logs | exec | console }"
    echo "  restart <service>"
    echo "  exec <service> <command>"
    echo "  console <service>"
    exit 1
fi

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
etcdir=$(pushd "${execdir}/../etc" >/dev/null ; pwd ; popd >/dev/null)
dockerdir=$(pushd "${execdir}/.." >/dev/null ; pwd ; popd >/dev/null)

ENV_NAME=$(expr `basename $0` : '\(.*\).sh')
echo "Environment: ${ENV_NAME}"
. ${etcdir}/${ENV_NAME}.env

export ENV_NAME
export ENV_TYPE
export VERSION
export CMS_BACKEND_NET
export HOERBUCHKATALOG_BACKEND_NET
export RPROXY_IP
export RPROXY_PUBLIC_HTTP_PORT
export RPROXY_PUBLIC_HTTPS_PORT
export JOOMLA_IP
export JOOMLA_PORT
export JOOMLADB_IP
export JOOMLADB_PORT
export ASSETS_IP
export ASSETS_PUBLIC_PORT
#export VAULT_IP
#export VAULT_PORT
export HOERBUCHKATALOG_IP
export HOERBUCHKATALOG_PORT
export DATATRANSFER_PUBLIC_PORT
export ADMIN_PUBLIC_PORT

function bookworm_docker() {
    docker-compose \
        -p ${ENV_NAME} \
        -f ${dockerdir}/cms/docker-compose.yml \
        -f ${dockerdir}/hoerbuchkatalog/docker-compose.yml \
        -f ${dockerdir}/rproxy/docker-compose.yml \
        "$@"
}

mode=${1:-}
shift
case "${mode}" in
    assembly)
        prjdir=$(pushd ${execdir}/../../ >/dev/null ; pwd ; popd >/dev/null)
        pushd "${prjdir}" >/dev/null || exit
        ./build.sh assembly
        popd >/dev/null || exit
    ;;
    build-images)
        #docker build \
        #    -t wbhonline/sshd-alpine:$(cat "${dockerdir}/sshd-alpine/.version") \
        #    ${dockerdir}/sshd-alpine
        bookworm_docker build \
            --build-arg ENV_NAME="${ENV_NAME}" \
            --build-arg ENV_TYPE="${ENV_TYPE}" \
            --compress
    ;;
    up)
        bookworm_docker up -d "$@"
    ;;
    start)
        # TODO In .env verschieben? Dann ist dc.sh allgemein einsetzbar
        bookworm_docker up --no-start
        bookworm_docker start admin
        bookworm_docker exec admin /opt/bookworm/bin/mkdirs.sh
        bookworm_docker exec admin /opt/bookworm/bin/perms.sh
        if [[ -f conf/secrets.json ]]
        then
            docker cp conf/secrets.json "${ENV_NAME}"_admin_1:/opt/bookworm/conf
        fi
        bookworm_docker start joomla-db
        sleep 10
        bookworm_docker start joomla
        sleep 10
        phpfpm_pool_conf=${dockerdir}/cms/www-${ENV_NAME}.conf
        if [[ -f ${phpfpm_pool_conf} ]]
        then
            docker cp "${phpfpm_pool_conf}" "${ENV_NAME}"_joomla_1:/usr/local/etc/php-fpm.d/www.conf
        fi
        bookworm_docker start
    ;;
    stop)
        bookworm_docker stop "$@"
    ;;
    restart)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 restart <service ...>"
            exit 1
        fi
        bookworm_docker restart "$@"
    ;;
    down)
        bookworm_docker down "$@"
    ;;
    ps)
        bookworm_docker ps
    ;;
    logs)
        bookworm_docker logs "$@"
    ;;
    volumes)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 volumes <container>"
            exit 1
        fi
        docker inspect "$1" -f '{{json .Mounts}}' | jq
    ;;
    exec)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 exec <docker exec args>"
            exit 1
        fi
        bookworm_docker exec "$@"
    ;;
    console)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 console <container>"
            exit 1
        fi
        bookworm_docker exec "$1" sh
    ;;
    console-broken)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 console-broken <container>"
            exit 1
        fi
        # TODO container_status=$(docker ps -a --filter name=$1 --format '{{.Status}}')
        docker commit "$1" "$1_broken" && docker run -it "$1_broken" sh
    ;;
    clear-logs)
        find /var/lib/docker/containers/ -type f -name "*.log" -delete
    ;;
    archive-all-logs)
        for container in $(docker ps -aq)
        do
            logfile=$(docker inspect ${container} --format='{{.LogPath}}')
            container_name=$(docker inspect ${container} --format='{{.Name}}' | sed -Ee 's#/(.*)#\1#')
            logfile_name=${container_name}-$(date +%Y%m%d_%H%M%S).log
            echo "Archiving logfile of ${container_name} to ${logfile_name}"
            mkdir -p ${HOME}/backup
            sudo cat ${logfile} | gzip -9 >${HOME}/backup/${logfile_name}.gz
            sudo truncate -s 0 ${logfile}
        done
    ;;
    show-log)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 show-log <container>"
            exit 1
        fi
        logfile=$(docker inspect ${container} --format='{{.LogPath}}')
    ;;
    backup)
        if [[ $# -lt 1 ]]
        then
            echo "usage: $0 backup <container>"
            exit 1
        fi
        container=$1
        backup_container=${container}_backup
        docker commit --pause=false ${container} ${backup_container}
        docker save -o ${backup_container}.tar ${backup_container}
        gzip -9 ${backup_container}.tar
        # TODO Backup its volumes
        #docker inspect ${container} --format '{{.Mounts}}'
    ;;
    backup-mysql)
        if [[ $# -lt 2 ]]
        then
            echo "usage: $0 backup-mysql <container> <database>"
            exit 1
        fi
        container=$1 ; shift
        database=$1
        # TODO https://hub.docker.com/r/deitch/mysql-backup/
        #docker run -d --restart=always \
        #    -e DB_DUMP_FREQ=60 \
        #    -e DB_DUMP_BEGIN=2330 \
        #    -e DB_DUMP_TARGET=/db \
        #    -e DB_SERVER=$1 \
        #    -v ${DOCKER_BACKUP_DIR}:/db \
        #    databack/mysql-backup
        # Setup /root/.my.cnf, section [client] in container before
        docker exec ${container} \
            /usr/bin/mysqldump ${container} \
            | gzip -9 >${DOCKER_BACKUP_DIR}/mysql-${database}-$(date +%Y%m%d_%H%M%S).sql.gz
    ;;
    health)
        docker inspect --format='{{json .State.Health}}'
    ;;
    compose)
        bookworm_docker "$@"
    ;;
    *)
        echo "usage: $0 ..."
        exit 1
    ;;
esac
