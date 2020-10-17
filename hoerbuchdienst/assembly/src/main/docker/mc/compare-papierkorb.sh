#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 4 ]]; then
  echo "usage: $0 <Src Shard> <Titelnummer Start> <Titelnummer Ende> <Dst Shard>"
  echo "  <Src | Dst Shard>    shard1, shard2, ..."
  exit 1
fi

SRC_SHARD="$1"
TITELNUMMER_START="$2"
TITELNUMMER_ENDE="$3"
DST_SHARD="$4"

src_papierkorb="${SRC_SHARD}/papierkorb"
dst="${DST_SHARD}/hoerbuchdienst"

function compare() {
  local src="$1"
  local dst="$2"
  if mc stat "${src_daisy}" >/dev/null 2>&1; then
    src_size="$(mc du "${src}" | awk '{ print $1 }')"
    dst_size="$(mc du "${dst}" | awk '{ print $1 }')"
    if [[ "${src_size}" != "${dst_size}" ]]; then
      return 1
    else
      return 0
    fi
  else
    echo "${src} does not exist"
    return 0
  fi
}

for titelnummer in $(seq "${TITELNUMMER_START}" "${TITELNUMMER_ENDE}"); do
  echo "!!!"
  echo "!!! Comparing ${titelnummer} from ${SRC_SHARD}/papierkorb with ${DST_SHARD}"
  echo "!!!"
  src_daisy="${src_papierkorb}/${titelnummer}DAISY"
  dst_daisy="${dst}/${titelnummer}DAISY"
  if ! compare "${src_daisy}" "${dst_daisy}"; then
    echo "Size of ${src_daisy} and ${dst_daisy} is unequal"
    echo "Copying ${src_daisy} to ${dst_daisy}"
    mc cp --recursive --continue --preserve "${src_daisy}" "${dst_daisy}"
    if compare "${src_daisy}" "${dst_daisy}"; then
      echo "Sizes are equal, removing ${src_daisy} (TODO)"
      #mc rm --force --recursive "${src_daisy}"
    fi
  fi
done

exit 0
