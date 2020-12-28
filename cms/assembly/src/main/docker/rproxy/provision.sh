#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function disable_site_conf() {
  local site="$1"
  if [[ -f "/etc/nginx/conf.d/${site}.conf" ]]; then
    echo "Disabling reverse proxy site ${site}"
    mv "/etc/nginx/conf.d/${site}.conf" "/etc/nginx/conf.d/${site}.conf.disabled"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      exit 1
    fi
  else
    echo "Reverse proxy site ${site} already disabled"
  fi
}

function enable_site_conf() {
  local site="$1"
  if [[ -f "/etc/nginx/conf.d/${site}.conf.disabled" ]]; then
    echo "Enabling nginx site ${site}"
    mv "/etc/nginx/conf.d/${site}.conf.disabled" "/etc/nginx/conf.d/${site}.conf"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      disable_nginx_conf "${site}"
      exit 1
    fi
  else
    echo "No unactivated reverse proxy site configuration for ${site} found"
  fi
}

function disable_stream_conf() {
  local site="$1"
  if [[ -f "/etc/nginx/stream.d/${site}.stream" ]]; then
    echo "Disabling TCP proxy ${site}"
    mv "/etc/nginx/stream.d/${site}.stream" "/etc/nginx/stream.d/${site}.stream.disabled"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      exit 1
    fi
  else
    echo "TCP proxy ${site} already disabled"
  fi
}

function enable_stream_conf() {
  local site="$1"
  if [[ -f "/etc/nginx/stream.d/${site}.stream.disabled" ]]; then
    echo "Enabling TCP proxy ${site}"
    mv "/etc/nginx/stream.d/${site}.stream.disabled" "/etc/nginx/stream.d/${site}.stream"
    if nginx -t && nginx -s reload; then
      echo "done"
    else
      echo "failed"
      disable_stream_conf "${site}"
      exit 1
    fi
  else
    echo "No unactivated TCP proxy site configuration for ${site} found"
  fi
}

function show_usage() {
  echo "usage: $0 [ -e <type> | -d <type> ] <server1[ server2 .. serverN]>"
  echo "   -e        enable configuration for <type>"
  echo "   -d        disable configuration for <type>"
  echo "   <type>    site | stream"
}

if [[ $# -lt 2 ]]; then
  show_usage
  exit 1
fi

declare MODE=
declare TYPE=
while getopts e:d: opt; do
  case "${opt}" in
    e)
      MODE="enable"
      TYPE="${OPTARG}"
      ;;
    d)
      MODE="disable"
      TYPE="${OPTARG}"
      ;;
    *)
      echo "Unknown option ${opt}"
      show_usage
      exit 1
      ;;
  esac
done
shift $((OPTIND-1))

for site in "$@"; do
  "${MODE}_${TYPE}_conf" "${site}"
done

exit 0
