#!/usr/bin/env bash
#
# Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <env> <project>"
  echo "  env        dev | prod"
  echo "  project    hbk | hbd"
  exit 1
fi
env=$1
shift
project=$1
project_name="${env}-${project}"

running_version="$(docker ps --format "{{.Image}}" --filter "name=${project_name}/*" |
  grep -Eow -e '([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}Z)' |
  uniq)"
if [[ -n "${running_version}" ]]; then
  echo "Running version is ${running_version}"
  grep_running_version="grep -v ${running_version}"
else
  grep_running_version="cat" # just pipe through
fi

function clean_old_releases() {
  local artifact="$1"
  echo "Cleaning old ~/releases"
  old_releases=("$(find ~/releases -maxdepth 1 -name "${env}-${project}*" -type d |
    "${grep_running_version}" |
    sort)")
  for old_release in ${old_releases[*]}; do
    echo "Removing ${old_release}"
    rm -rf "${old_release:?}/${artifact}"
  done
  echo "done"
}

case "${project}" in
  hbk)
    echo "Cleaning unused Docker images"
    docker image rm "$(
      docker images "wbh-cms/*" |
        ${grep_running_version} |
        grep -v "REPOSITORY" |
        awk '{print $1":"$2}'
    )"
    docker image rm "$(
      docker images "${project_name}/*" |
        ${grep_running_version} |
        grep -v "REPOSITORY" |
        awk '{print $1":"$2}'
    )"
    echo "done"
    clean_old_releases "wbh.bookworm.hoerbuchkatalog.deployment" "wbh.bookworm.cms.assembly"
    ;;
  hbd)
    echo "Cleaning unused Docker images"
    docker image rm "$(
      docker images "${project_name}/*" |
        ${grep_running_version} |
        grep -v "REPOSITORY" |
        awk '{print $1":"$2}'
    )"
    echo "done"
    clean_old_releases "wbh.bookworm.hoerbuchdienst.assembly"
    ;;
  *)
    echo "Unknown project ${project}"
    exit 1
    ;;
esac

exit 0
