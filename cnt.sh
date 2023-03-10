#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# -lt 3 ]]; then
  echo "usage: $0 <env> <project> <command>"
  echo "  env        dev | prod"
  echo "  project    hbk | hbd"
  echo "  command    Docker Compose command"
  exit 1
fi
env=$1
shift
project=$1
shift
project_name="${env}-${project}"

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"
releasedir="${execdir}/../releases"
releasedir="$(
  pushd "${execdir}/../releases" >/dev/null
  pwd
  popd >/dev/null
)"
running_version="$(docker ps --format "{{.Image}}" --filter "name=${project_name}/*" |
  grep -Eow -e '([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}Z)' |
  uniq)"
#if [[ -z "${running_version}" ]]; then
#  echo "No running version found"
#  exit 1
#fi

dc="docker-compose -p ${env}-${project}"

case "${project}" in
  cms)
    artifacts=("wbh.bookworm.cms.assembly")
    running_version="$(docker ps --format "{{.Image}}" --filter "name=${env}-hbk/*" |
      grep -Eow -e '([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}Z)' |
      uniq)"
    if [[ -z "${running_version}" ]]; then
      echo "No running version found"
      exit 1
    fi
    current_project_dir="${releasedir}/${env}-hbk-${running_version}"
    dc="docker-compose -p ${env}-hbk"
    pushd "${current_project_dir}" >/dev/null
    for artifact in "${artifacts[@]}"; do
      pushd "${artifact}" >/dev/null
      echo "Executing for version ${running_version}"
      ${dc} "$@"
      popd >/dev/null
    done
    popd >/dev/null
    ;;
  hbk)
    if [[ -z "${running_version}" ]]; then
      echo "No running version found"
      exit 1
    fi
    artifacts=("wbh.bookworm.hoerbuchkatalog.deployment")
    current_project_dir="${releasedir}/${project_name}-${running_version}"
    pushd "${current_project_dir}" >/dev/null
    for artifact in "${artifacts[@]}"; do
      pushd "${artifact}" >/dev/null
      echo "Executing for version ${running_version}"
      ${dc} "$@"
      popd >/dev/null
    done
    popd >/dev/null
    ;;
  hbd)
    if [[ -z "${running_version}" ]]; then
      echo "No running version found"
      exit 1
    fi
    artifacts=("wbh.bookworm.hoerbuchdienst.assembly")
    current_project_dir="${releasedir}/${project_name}-${running_version}"
    pushd "${current_project_dir}" >/dev/null
    for artifact in "${artifacts[@]}"; do
      pushd "${artifact}" >/dev/null
      echo "Executing for version ${running_version}"
      ${dc} "$@"
      popd >/dev/null
    done
    popd >/dev/null
    ;;
  *)
    echo "Unknown project: ${project}"
    exit 1
    ;;
esac

exit 0
