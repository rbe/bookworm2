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
[[ ! -d "${releasedir}" ]] && mkdir "${releasedir}"
releasedir="$(
  pushd "${execdir}/../releases" >/dev/null
  pwd
  popd >/dev/null
)"
project_dir="${releasedir}/${project_name}-${timestamp}"
if [[ ! -d "${project_dir}" ]]; then
  mkdir "${project_dir}"
fi

function deploy_artifacts() {
  local artifacts=("$@")
  echo "Deploying artifacts ${artifacts[*]}"
  for artifact in "${artifacts[@]}"; do
    echo "Deploying WBH Bookworm ${env} ${artifact}"
    file="${assemblydir}/${artifact}-${timestamp}"
    if [[ ! -d "${project_dir}/${artifact}" ]]; then
      unzip "${file}.zip" \
        docker-compose.yml docker-compose.${env}.yml .env \
        lifecycle.sh backup.sh setup-ufw.sh \
        -d "${project_dir}/${artifact}"
    else
      echo "Artifact ${artifact} already exists hoerbuchkatalog ${project_dir}/${artifact}"
    fi
  done
  echo "done"
}

case "${project}" in
  hbk)
    artifacts=("wbh.bookworm.hoerbuchkatalog.deployment" "wbh.bookworm.cms.assembly")
    deploy_artifacts "${artifacts[@]}"
    for artifact in "${artifacts[@]}"; do
      pushd "${project_dir}/${artifact}" >/dev/null
      chmod +x *.sh
      echo "Starting ${artifact}"
      docker-compose \
        -p "${project_name}" \
        -f docker-compose.yml -f docker-compose."${env}".yml \
        up -d --no-build
      popd >/dev/null
      echo "done"
    done
    ;;
  hbd)
    artifacts=("wbh.bookworm.hoerbuchdienst.assembly")
    deploy_artifacts "${artifacts[@]}"
    pushd "${project_dir}/wbh.bookworm.hoerbuchdienst.assembly" >/dev/null
    chmod +x *.sh
    if ! docker volume ls | grep -c "${project_name}_minio" >/dev/null; then
      echo "Provisioning ${project}, environment ${env} (${project_name})"
      "${execdir}"/lifecycle.sh provision
      "${execdir}"/backup.sh
      "${execdir}"/setup-ufw.sh
      echo "done"
    else
      echo "Won't provision ${project_name}, there are volumes present already"
      echo "Execute $(pwd)/lifecycle.sh at your own (risk)"
    fi
    popd >/dev/null
    ;;
  *)
    echo "Unknown project: ${project}"
    exit 1
    ;;
esac

exit 0
