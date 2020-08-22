#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <Titelnummer> <shard>"
  echo "  Titelnummer    Identifier of audiobook"
  echo "  shard          mc host alias for shard, see 'mc config host list'"
  echo ""
  echo "  Copies ZIP archive of audiobook <Titelnummer> from bucket 'eingangskorb',"
  echo "  unpacks it and moves files into bucket 'hoerbuchdienst'"
  echo "  on Shard <shard>"
  exit 1
fi

TITELNUMMER="$1"
shift
SHARD="$1"
pushd /var/local/mc >/dev/null
mc cat minio/eingangskorb/"${TITELNUMMER}.zip" |
  unzip -d . - &&
  mc mv --recursive "${TITELNUMMER}Kapitel" "${SHARD}"/hoerbuchdienst
popd >/dev/null

exit 0
