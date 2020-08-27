#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function show_usage() {
  echo "usage: $0 <shard> <Titelnummer1> [<TitelnummerN> ...]"
  echo "  shard          mc host alias for shard, see 'mc config host list'"
  echo "  Titelnummer    Identifier of audiobook"
  echo ""
  echo "  Copies ZIP archive of audiobook(s) <Titelnummer> from bucket 'eingangskorb',"
  echo "  unpacks and moves files into bucket 'hoerbuchdienst' on Shard <shard>"
  exit 1
}

function mandant_wbh() {
  local titelnummer="$1"
  shift
  local tmpdir="$1"
  if [[ -d "${tmpdir}/${titelnummer}" ]]; then
    mv "${tmpdir}/${titelnummer}" "${tmpdir}/${titelnummer}Kapitel"
  fi
}

function move() {
  local titelnummer="$1"
  local shard="$2"
  zip="minio/eingangskorb/${titelnummer}.zip"
  tmpdir="/var/local/mc/${titelnummer}_zip"
  dir="${titelnummer}Kapitel"
  dst="${shard}/hoerbuchdienst"
  mc stat "${zip}"
  # shellcheck disable=SC2181
  if [[ $? == 0 ]]; then
    mkdir "${tmpdir}"
    echo "Unpacking ${titelnummer} in ${tmpdir}"
    mc cat "${zip}" | unzip -d "${tmpdir}" -
    mandant_wbh "${titelnummer}" "${tmpdir}"
    if [[ -d "${tmpdir}/${dir}" ]]; then
      pushd "${tmpdir}" >/dev/null
      mc mv --recursive "${dir}" "${dst}"
      popd >/dev/null
    else
      echo "Could not unzip ${zip} into ${dir}"
      exit 1
    fi
    rm -rf "${tmpdir}"
  else
    echo "${zip} not found"
    exit 1
  fi
}

if [[ $# -lt 2 ]]; then
  show_usage
fi

SHARD="$1"
shift
TITELNUMMERN=("$@")

echo "Moving ${#TITELNUMMERN[@]} audiobooks to ${SHARD}"
for t in "${TITELNUMMERN[@]}"; do
  echo "Moving ${t} to ${SHARD}"
  move "${t}" "${SHARD}"
  echo "done"
done

exit 0
