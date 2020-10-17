#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function show_usage() {
  echo "usage: $0 <Dst Shard> <Name im Eingangskorb 1> [<Name im Eingangskorb N> ...]"
  echo "  Dst Shard      Ziel-Shard (mc host alias for shard, see 'mc config host list')"
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
  zipdownload="/var/local/mc/${ident}.zip"
  tmpdir="/var/local/mc/${ident}_zip"
  daisydir="${ident}DAISY"
  dst="${shard}/hoerbuchdienst"
  # shellcheck disable=SC2181
  if mc stat "${zip}"; then
    if [[ -d "${tmpdir}" || -f "${zipdownload}" ]]; then
      echo "Please cleanup possibly existing directories or files:"
      echo "  - ${tmpdir}"
      echo "  - ${zipdownload}"
      exit 1
    fi
    if mc stat "${dst}/${daisydir}" 2>/dev/null; then
      #echo "${ident} already exists at ${dst}/${daisydir}"
      #echo "Please remove for update"
      #exit 1
      echo "Removing ${dst}/${daisydir} as it already exists"
      mc rm --force --recursive "${dst}/${daisydir}"
    fi
    echo "Moving ${t} to ${SHARD}"
    mkdir "${tmpdir}"
    echo "Copying ${ident} to ${zipdownload}"
    mc cp "${zip}" "${zipdownload}"
    echo "done"
    echo "Unpacking ${ident} in ${tmpdir}"
    unzip -d "${tmpdir}" "${zipdownload}"
    echo "done"
    mandant_wbh "${ident}" "${tmpdir}"
    num_files_in_zip="$(unzip -Z "${zipdownload}" | grep -cE "^(d|-).*")"
    num_extracted_files="$(find "${tmpdir}/${ident}DAISY" | wc -l)"
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
      rm "${zipdownload}"
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
  echo "done"
}

if [[ $# -lt 2 ]]; then
  show_usage
fi

SHARD="$1"
shift
TITELNUMMERN=("$@")

echo "Moving ${#TITELNUMMERN[@]} audiobooks to ${SHARD}"
for t in "${TITELNUMMERN[@]}"; do
  unpack "${t}" "${SHARD}"
  echo "Waiting 1 second"
  sleep 1
done

exit 0
