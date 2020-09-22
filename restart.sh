#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
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

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd >/dev/null
)"
assemblydir="$(
  pushd "${execdir}/assembly/target/dependency" >/dev/null
  pwd
  popd >/dev/null
)"
timestamp="$(find "${assemblydir}" -name \*.zip |
  head -1 |
  sed -E 's/.*([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}Z).*/\1/')"
releasedir="${execdir}/../releases"
releasedir="$(
  pushd "${execdir}/../releases" >/dev/null
  pwd
  popd >/dev/null
)"
new_project_dir="${releasedir}/${project_name}-${timestamp}"
running_version="$(docker ps --format "{{.Image}}" --filter "name=${project_name}/*" |
  grep -Eow -e '([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}-[0-9]{2}-[0-9]{2}Z)' |
  uniq)"
if [[ -n "${running_version}" ]]; then
  current_project_dir="${releasedir}/${project_name}-${running_version}"
else
  current_project_dir=""
fi

case "${project}" in
  hbk)
    artifacts=("wbh.bookworm.hoerbuchkatalog.deployment" "wbh.bookworm.cms.assembly")
    if [[ -n "${current_project_dir}" ]]; then
      pushd "${current_project_dir}" >/dev/null
      for artifact in "${artifacts[@]}"; do
        pushd "${artifact}" >/dev/null
        echo "Stopping artifact ${artifact} in $(pwd)"
        ./lifecycle.sh stop
        popd >/dev/null
      done
      popd >/dev/null
    fi
    pushd "${new_project_dir}" >/dev/null
    for artifact in "${artifacts[@]}"; do
      pushd "${artifact}" >/dev/null
      echo "Starting artifact ${artifact} in $(pwd)"
      ./lifecycle.sh start
      popd >/dev/null
    done
    popd >/dev/null
    ;;
  hbd)
    artifacts=("wbh.bookworm.hoerbuchdienst.assembly")
    if [[ -n "${current_project_dir}" ]]; then
      pushd "${current_project_dir}" >/dev/null
      for artifact in "${artifacts[@]}"; do
        pushd "${artifact}" >/dev/null
        echo "Stopping artifact ${artifact} in $(pwd)"
        ./lifecycle.sh stop
        popd >/dev/null
      done
      popd >/dev/null
    fi
    pushd "${new_project_dir}" >/dev/null
    for artifact in "${artifacts[@]}"; do
      pushd "${artifact}" >/dev/null
      echo "Starting artifact ${artifact} in $(pwd)"
      ./lifecycle.sh start
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
