#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

PROJECT_NAME="${docker.project.name}"

function enable_nginx_conf {
    local server="$1"
    echo "Enabling reverse proxy server ${server}"
    docker-compose -p "${PROJECT_NAME}" exec rproxy \
        mv "/etc/nginx/conf.d/${server}.conf.disabled" "/etc/nginx/conf.d/${server}.conf"
    echo "done"
}

for server in "$@"
do
    enable_nginx_conf "${server}"
done

exit 0
