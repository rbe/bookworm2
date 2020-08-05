#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

BASE_URL="http://localhost:8080"
MANDANT="WBH"
HOERERNUMMER="80170"

set -o nounset
set -o errexit

function download_zip() {
  local titlenummer="$1"
  echo "Downloading audiobook ${titelnummer}"
  curl -v -X POST -H "Content-Type: application/json" --output "${titlenummer}".zip --data "
{
    \"mandant\": \"${MANDANT}\",
    \"hoerernummer\": \"${HOERERNUMMER}\",
    \"aghNummer\": \"\",
    \"titelnummer\": \"${titlenummer}\"
}" "${BASE_URL}/stream/zip"
}

for titelnummer in "$@"; do
  download_zip "${titelnummer}"
done

exit 0
