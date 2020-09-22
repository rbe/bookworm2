#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"

function show_usage {
  echo "usage: $0 <destroy | provision | start | stop>"
  exit 1
}

[[ $# != 1 ]] && show_usage

function wait_for_container {
  local cnt="$1"
  local timeout="$2"
  while ! docker ps | grep -c "${cnt}" >/dev/null; do
    echo "Waiting ${timeout} seconds maximum for container ${cnt}"
    sleep 1
    timeout=$((timeout - 1))
    if [[ ${timeout} == 0 ]]; then
      echo "Timeout of ${timeout} seconds reached waiting for container ${cnt}"
      break
    fi
  done
}

mode="$1"
shift
env="${docker.project.env}"
project="${docker.project.name}"

domain_name="${domain}"
tld="${domain_name/*./}"
dc="docker-compose -p ${env}-${project} -f docker-compose.yml -f docker-compose.${env}.yml"
case "${mode}" in
  destroy)
    [[ $# -eq 1 ]] && all="$1" || all="no"
    pushd "${execdir}" >/dev/null
    docker-compose -p "${project}" down
    set +o errexit
    docker system prune -f
    if [[ "${all}" == "all" ]]; then
      docker volume ls |
        grep hbd |
        awk '{print $2}' |
        xargs docker volume rm
    else
      docker volume ls |
        grep hbd |
        grep -vE "(rproxycerts)" |
        awk '{print $2}' |
        xargs docker volume rm
    fi
    docker image ls |
      grep wbh- |
      grep "${HBK_RELEASE}" |
      awk '{print $1":"$2}' |
      xargs docker image rm
    set -o errexit
    popd >/dev/null
    ;;
  provision)
    echo "Nothing to do"
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
    show_usage
    ;;
esac

exit 0
