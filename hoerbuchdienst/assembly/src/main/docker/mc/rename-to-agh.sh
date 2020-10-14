#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 <Titelnummer>"
  exit 1
fi

TITELNUMMER="$1"

# Titelnummer Spalte 5-9, AGH Nummer ab Spalte 1018
AGHNUMMER=$(mc cat minio/hoerbuchkatalog/Gesamt.dat |
  dos2unix |
  awk '{
    titelnummer = substr($0, 5, 5);
    aghnummer = substr($0, 1018);
    print titelnummer","aghnummer;
  }' |
  grep "${TITELNUMMER}" |
  awk -F, '{print $2}')

SUFFIX="DAISY"

src="minio/hoerbuchdienst/${TITELNUMMER}${SUFFIX}"
dst="minio/hoerbuchdienst/${AGHNUMMER}${SUFFIX}"
if mc stat "${src}"; then
  echo "Renaming ${src} to ${dst}"
  mc mv --recursive "${src}" "${dst}"
else
  echo "Cannot find ${src}"
fi

exit 0
