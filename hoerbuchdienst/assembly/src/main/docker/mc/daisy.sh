#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function show_usage() {
  echo "usage: $0 -o <operation> -s <Source Shard> -d <Dest Shard>"
  echo "          -t <Titelnummer Start> [-c <Anzahl Titelnummern> | -u <Titelnummer Ende>]"
  echo "       -o <operation>"
  echo "          Gewünschte Operation:"
  echo "             move-eingangskorb"
  echo "             unpack-eingangskorb"
  echo "             move-daisy"
  echo "             compare-papierkorb"
  echo "             purge-papierkorb"
  echo "       -s <Source Shard>"
  echo "          Shard auf dem das Hörbuch liegt"
  echo "       -d <Dest Shard>"
  echo "          Shard auf den das Hörbuch übertragen werden soll"
  echo "       -t <Titelnummer Start>"
  echo "       -c <Anzahl Titelnummern>"
  echo "       -u <Titelnummer Ende>"
}

OPERATION=
DST_SHARD=
SRC_SHARD=

if [[ $# -lt 1 ]]; then
  show_usage
fi

while getopts d:m:s: opt; do
  case "${opt}" in
    o) OPERATION="${OPTARG}" ;;
    d) DST_SHARD="${OPTARG}" ;;
    s) SRC_SHARD="${OPTARG}" ;;
    *)
      echo "Unknown option ${opt}"
      show_usage
      exit 1
      ;;
  esac
done

case "${OPERATION}" in
  move-eingangskorb) ;;
  unpack-eingangskorb) ;;
  move-daisy) ;;
  compare-parpierkorb) ;;
  purge-parpierkorb) ;;
esac

exit 0
