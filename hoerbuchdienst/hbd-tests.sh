#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

function download_zip() {
  local titlenummer="$1"
  echo "Downloading audiobook ${titelnummer}"
  curl -v -X POST -H "Content-Type: application/json" --output "${titlenummer}".zip --data "
{
    \"mandant\": \"WBH\",
    \"hoerernummer\": \"00001\",
    \"aghNummer\": \"\",
    \"titelnummer\": \"${titlenummer}\"
}" http://localhost:8080/stream/zip
}

for titelnummer in "$@"; do
  download_zip "${titelnummer}"
done

exit 0
