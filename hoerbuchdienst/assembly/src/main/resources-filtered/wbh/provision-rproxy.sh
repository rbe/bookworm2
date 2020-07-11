#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <server1[ server2 serverN]>"
  exit 1
fi

PROJECT_NAME="${docker.project.name}"

function enable_nginx_conf {
  local server="$1"
  echo "Enabling reverse proxy server ${server}"
  docker-compose -p "${PROJECT_NAME}" \
    exec hbd-rproxy \
    mv "/etc/nginx/conf.d/${server}.conf.disabled" "/etc/nginx/conf.d/${server}.conf"
  echo "done"
}

for server in "$@"
do
  enable_nginx_conf "${server}"
done

exit 0
