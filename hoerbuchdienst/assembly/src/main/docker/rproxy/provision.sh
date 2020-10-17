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
    echo "Enabling nginx server ${server}"
    mv "/etc/nginx/conf.d/${server}.conf.disabled" "/etc/nginx/conf.d/${server}.conf"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      exit 1
    fi
  else
    echo "Reverse proxy server ${server} already enabled"
  fi
  if [[ -f "/etc/nginx/stream.d/${server}.stream.disabled" ]]; then
    echo "Enabling TCP proxy ${server}"
    mv "/etc/nginx/stream.d/${server}.stream.disabled" "/etc/nginx/stream.d/${server}.stream"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      exit 1
    fi
  else
    echo "TCP proxy ${server} already enabled"
  fi
}

for server in "$@"; do
  enable_nginx_conf "${server}"
done

exit 0
