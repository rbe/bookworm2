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
dst_papierkorb="${DST_SHARD}/papierkorb"
dst="${DST_SHARD}/hoerbuchdienst"

for titelnummer in $(seq "${TITELNUMMER_START}" "${TITELNUMMER_ENDE}"); do
  echo "!!!"
  echo "!!! Moving ${titelnummer} from ${SRC_SHARD} to ${DST_SHARD}"
  echo "!!!"
  src_daisy="${SRC_SHARD}/hoerbuchdienst/${titelnummer}DAISY"
  dst_daisy="${dst}/${titelnummer}DAISY"
  echo "${src_daisy} -> ${dst_daisy}"
  if mc stat "${src_daisy}" >/dev/null 2>&1; then
    #if mc stat "${dst_daisy}" >/dev/null 2>&1; then
    #  echo "Removing ${titelnummer} on ${DST_SHARD} as it already exists"
    #  mc mv --recursive "${dst_daisy}" "${dst_papierkorb}"
    #  if ! mc stat "${dst_daisy}" >/dev/null 2>&1; then
    #    echo "Removed ${titelnummer} successfully on ${DST_SHARD}"
    #  else
    #    echo "Removing ${titelnummer} on ${DST_SHARD} failed"
    #    exit 1
    #  fi
    #fi
    echo "Copying ${titelnummer} to ${DST_SHARD}"
    if mc cp --recursive --continue --preserve "${src_daisy}" "${dst}"; then
      echo "done"
      echo "Removing ${titelnummer} on ${SRC_SHARD}"
      mc mv --recursive --continue --preserve "${src_daisy}" "${src_papierkorb}"
      echo ""
      echo "Waiting 1 second"
      sleep 1
      echo ""
    else
      echo "Copy of ${titelnummer} failed"
      exit 1
    fi
  else
    echo "${titelnummer} does not exist on ${SRC_SHARD}"
  fi
done

exit 0
