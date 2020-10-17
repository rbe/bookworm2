#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <server1[ server2 .. serverN]>"
  exit 1
fi

function disable_nginx_conf() {
  local server="$1"
  if [[ -f "/etc/nginx/conf.d/${server}.conf" ]]; then
    echo "Disabling reverse proxy server ${server}"
    mv "/etc/nginx/conf.d/${server}.conf" "/etc/nginx/conf.d/${server}.conf.disabled"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      exit 1
    fi
  else
    echo "Reverse proxy server ${server} already disabled"
  fi
  if [[ -f "/etc/nginx/stream.d/${server}.stream" ]]; then
    echo "Disabling TCP proxy ${server}"
    mv "/etc/nginx/stream.d/${server}.stream" "/etc/nginx/stream.d/${server}.stream.disabled"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      exit 1
    fi
  else
    echo "TCP proxy ${server} already disabled"
  fi
}

for server in "$@"; do
  enable_nginx_conf "${server}"
done

exit 0
