#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function show_usage() {
  echo "usage: $0 <shard> <Name im Eingangskorb 1> [<Name im Eingangskorb N> ...]"
  echo "  shard          Ziel-Shard (mc host alias for shard, see 'mc config host list')"
  echo "  Titelnummer    Name im Eingangskorb, ohne .zip"
  echo ""
  echo "  Copies ZIP archive of audiobook(s) <Titelnummer> from bucket 'eingangskorb',"
  echo "  unpacks and moves files into bucket 'hoerbuchdienst' on Shard <shard>"
  exit 1
}

function mandant_wbh() {
  local ident="$1"
  shift
  local tmpdir="$1"
  local n=""
  if [[ -d "${tmpdir}/${ident}Kapitel" ]]; then
    n="${tmpdir}/${ident}Kapitel"
  elif [[ -d "${tmpdir}/${ident}" ]]; then
    n="${tmpdir}/${ident}"
  fi
  mv "${n}" "${tmpdir}/${ident}DAISY"
}

function unpack() {
  local ident="$1"
  local shard="$2"
  zip="minio/eingangskorb/${ident}.zip"
  zipdownload="/var/local/mc/${zip}"
  tmpdir="/var/local/mc/${ident}_zip"
  daisydir="${ident}DAISY"
  dst="${shard}/hoerbuchdienst"
  mc stat "${zip}"
  # shellcheck disable=SC2181
  if [[ $? == 0 ]]; then
    if [[ -d "${tmpdir}" || -f "${zipdownload}" ]]; then
      echo "Please cleanup possibly existing directories or files:"
      echo "  - ${tmpdir}"
      echo "  - ${zipdownload}"
      exit 1
    fi
    if mc stat "${dst}/${daisydir}" 2>/dev/null; then
      echo "${ident} already exists at ${dst}/${daisydir}"
      echo "Please remove for update"
      exit 1
    fi
    mkdir "${tmpdir}"
    echo "Copying ${ident} to ${zipdownload}"
    mc cp "${zip}" "${zipdownload}"
    echo "done"
    echo "Unpacking ${ident} in ${tmpdir}"
    unzip -d "${tmpdir}" "${zipdownload}"
    echo "done"
    mandant_wbh "${ident}" "${tmpdir}"
    num_files_in_zip="$(unzip -Z /var/local/mc/"${zip}" | grep -cE "^(d|-).*")"
    num_extracted_files="$(find "${tmpdir}/${ident}" | wc -l)"
    if [[ "${num_files_in_zip}" != "${num_extracted_files}" ]]; then
      echo "${ident}: Number of files in ZIP (${num_files_in_zip}) differs to number of extracted files (${num_extracted_files})"
      exit 1
    fi
    if [[ -d "${tmpdir}/${daisydir}" ]]; then
      pushd "${tmpdir}" >/dev/null
      mc mv --recursive "${daisydir}" "${dst}"
      popd >/dev/null
      echo "Cleaning up temporary files"
      rm -rf "${tmpdir}"
      rm /var/local/mc/"${zip}"
      echo "done"
      if mc stat "${dst}/${daisydir}" 2>/dev/null; then
        echo "Removing ${zip}"
        mc rm "${zip}"
        echo "done"
      else
        echo "Cannot stat ${dst}, did not remove ${zip}"
      fi
    else
      echo "${ident}: Could not unzip ${zip} into ${daisydir}"
      exit 1
    fi
  else
    echo "${ident}: ${zip} not found"
    exit 1
  fi
}

if [[ $# -lt 2 ]]; then
  show_usage
fi

SHARD="$1"
shift
TITELNUMMERN=("$@")

echo "Moving ${#TITELNUMMERN[@]} audiobooks to ${SHARD}"
for t in "${TITELNUMMERN[@]}"; do
  echo "Moving ${t} to ${SHARD}"
  unpack "${t}" "${SHARD}"
  echo "done"
done

exit 0
