#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <server1[ server2 .. serverN]>"
  exit 1
fi

function enable_nginx_conf() {
  local server="$1"
  if [[ -f "/etc/nginx/conf.d/${server}.conf.disabled" ]]; then
    mv "/etc/nginx/conf.d/${server}.conf.disabled" "/etc/nginx/conf.d/${server}.conf"
    echo "Enabled reverse proxy server ${server}"
  else
    echo "Reverse proxy server ${server} enabled already"
  fi
}

for server in "$@"; do
  enable_nginx_conf "${server}"
done

exit 0