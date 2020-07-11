#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 <project>"
  echo "    project    hbk | hbd"
  exit 1
fi

PROJECT_NAME="${docker.project.name}"
project="$1"

function enable_nginx_conf {
  local server="$1"
  echo "Enabling reverse proxy server ${server}"
  docker-compose -p "${PROJECT_NAME}" exec "${project}-rproxy" \
      mv "/etc/nginx/conf.d/${server}.conf.disabled" "/etc/nginx/conf.d/${server}.conf"
  echo "done"
}

for server in "$@"
do
  enable_nginx_conf "${server}"
done

exit 0
