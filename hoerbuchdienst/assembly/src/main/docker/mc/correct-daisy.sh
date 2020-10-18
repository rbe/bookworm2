#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

src="minio/hoerbuchdienst"

for titelnummer in $(mc ls "${src}"); do
  double="${src}/${titelnummer}DAISY/${titelnummer}DAISY"
  single="${src}/${titelnummer}DAISY"
  if mc stat "${double}"; then
    echo "!!! Moving ${double} to ${single}"
    if mc cp --recursive "${double}" "${single}"; then
      echo "done"
    else
      echo "failed"
    fi
  fi
done

exit 0
