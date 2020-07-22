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
project_name="${env}-${project}"
running_version="$(docker ps --format "{{.Image}}" --filter "name=${project_name}/*" |
  grep -Eow -e '([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}Z)' |
  uniq)"
current_project_dir="${releasedir}/${project_name}-${running_version}"

dc="docker-compose -p ${env}-${project}"

case "${project}" in
  hbk)
    echo "Not implemented"
    exit 1
    ;;
  hbd)
    artifacts=("wbh.bookworm.hoerbuchdienst.assembly")
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
