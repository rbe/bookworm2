#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

OLD_SUFFIX="Kapitel"
NEW_SUFFIX="DAISY"

for daisydir in $(mc ls minio/hoerbuchdienst | grep "${OLD_SUFFIX}" | awk '{print $5}'); do
  echo "Renaming ${daisydir}"
  titelnummer=${daisydir/${OLD_SUFFIX}\//}
  src="minio/hoerbuchdienst/${daisydir}"
  dst="minio/hoerbuchdienst/${titelnummer}${NEW_SUFFIX}"
  if mc stat "${src}"; then
    mc mv --recursive "${src}" "${dst}"
  else
    echo "Cannot find ${daisydir}"
  fi
done

exit 0
