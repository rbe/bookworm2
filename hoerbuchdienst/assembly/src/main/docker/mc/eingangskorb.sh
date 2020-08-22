#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 <Titelnummer>"
  echo ""
  echo "  Copies ZIP archive of audiobook <Titelnummer> from bucket 'eingangskorb',"
  echo "  unpacks it and moves files into bucket 'hoerbuchdienst'"
  exit 1
fi

TITELNUMMER="$1"
mc cat minio/eingangskorb/"${TITELNUMMER}.zip" |
  unzip -d . - &&
  mc mv --recursive "${TITELNUMMER}Kapitel" minio/hoerbuchdienst

exit 0
