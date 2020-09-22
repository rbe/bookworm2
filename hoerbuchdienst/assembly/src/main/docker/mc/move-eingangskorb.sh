#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 3 ]]; then
  echo "usage: $0 <Titelnummer Start> <Anzahl> <Shard>"
  exit 1
fi

START="$1"
NUM="$2"
SHARD="$3"
dst="${SHARD}/eingangskorb"

mapfile -t TITELNUMMERN < <(titelnummern.sh "${START}" "${NUM}")

for titelnummer in "${TITELNUMMERN[@]}"; do
  titelnummer="$(printf "%05d" "${titelnummer}")"
  zip="minio/eingangskorb/${titelnummer}.zip"
  if mc stat "${zip}"; then
    echo "Copying ${titelnummer} to ${SHARD}"
    if mc cp "${zip}" "${dst}"; then
      echo "Removing ${titelnummer}"
      mc rm "${zip}"
      echo "Waiting 1 second"
      sleep 1
    else
      echo "Copy of ${titelnummer} failed"
      exit 1
    fi
  fi
done

exit 0
