#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 <Shard>"
  exit 1
fi

SHARD="$1"
SRC="${SHARD}/hoerbuchdienst"

for titelnummer in $(mc ls "${SRC}" | awk '{print $5}' | grep -E '^0{1,4}.{1,4}DAISY'); do
  neu="$(echo "${titelnummer}" | sed -E 's#^0{1,4}(.+)#\1#')"
  echo "${titelnummer} -> ${neu}"
  echo mc mv --recursive "${SRC}/${titelnummer}" "${SRC}/${neu}"
  sleep 5
done

exit 0
