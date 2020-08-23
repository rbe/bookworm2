#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 2 ]]; then
  echo "usage: $0 <shard> <Titelnummer1> [<TitelnummerN> ...]"
  echo "  shard          mc host alias for shard, see 'mc config host list'"
  echo "  Titelnummer    Identifier of audiobook"
  echo ""
  echo "  Copies ZIP archive of audiobook <Titelnummer> from bucket 'eingangskorb',"
  echo "  unpacks it and moves files into bucket 'hoerbuchdienst'"
  echo "  on Shard <shard>"
  exit 1
fi

function move() {
  local titelnummer="$1"
  local shard="$2"
  zip="minio/eingangskorb/${titelnummer}.zip"
  tmpdir="/var/local/mc/${titelnummer}_zip"
  dir="${titelnummer}Kapitel"
  dst="${shard}/hoerbuchdienst"
  if mc stat "${zip}" >/dev/null; then
    mkdir "${tmpdir}"
    mc cat "${zip}" | unzip -d "${tmpdir}" -
    if [[ -d "${tmpdir}/${dir}" ]]; then
      pushd "${tmpdir}" >/dev/null
      mc mv --recursive "${dir}" "${dst}"
      popd >/dev/null
    else
      echo "Could not unzip ${zip} into ${dir}"
      exit 1
    fi
    rmdir "${tmpdir}"
  else
    echo "${zip} not found"
    exit 1
  fi
}

SHARD="$1"
shift
TITELNUMMERN=("$@")

for t in "${TITELNUMMERN[@]}"; do
  move "${t}" "${SHARD}"
done

exit 0
