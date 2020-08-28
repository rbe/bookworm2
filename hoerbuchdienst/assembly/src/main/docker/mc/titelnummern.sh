#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <min> <num>"
  exit 1
fi

MIN=$1
NUM=$2

mapfile -t titelnummern < <(mc ls minio/eingangskorb | awk '{ gsub(/.zip/, "", $5); print $5; }')

declare -i i=0
for t in "${titelnummern[@]}"; do
  n=$((10#$t))
  if [[ ${n} -ge ${MIN} ]]; then
    echo "${n}"
    i=$((i + 1))
  fi
  if [[ "${i}" == "${NUM}" ]]; then
    exit 0
  fi
done

exit 0
