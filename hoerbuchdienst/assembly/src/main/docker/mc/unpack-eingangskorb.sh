#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function show_usage() {
  echo "usage: $0"
  echo "  $0 minio $(titelnummern.sh 1 100 | sort -g)"
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
  elif [[ -d "${tmpdir}/${ident}kapitel" ]]; then
    n="${tmpdir}/${ident}kapitel"
  elif [[ -d "${tmpdir}/${ident}" ]]; then
    n="${tmpdir}/${ident}"
  fi
  # Remove unwanted files
  # See below: copy tmpdir_1 to tmpdir_2
  #find "${n}" -type f -name \*.txt -print0 | xargs -r -0 rm
  #find "${n}" -type f -name \*.lnk -print0 | xargs -r -0 rm
  #find "${n}" -type d -mindepth 1 -print0 | xargs -r -0 rm -rf
  if [[ -d "${n}" ]]; then
    mv "${n}" "${tmpdir}/${ident}DAISY"
  fi
}

function unpack() {
  local ident="$1"
  local shard="$2"
  zip="minio/eingangskorb/${ident}.zip"
  zipdownload="/var/local/mc/${ident}.zip"
  tmpdir="/var/local/mc/${ident}_unpack"
  daisydir="${ident}DAISY"
  dst="${shard}/hoerbuchdienst"
  # shellcheck disable=SC2181
  if mc stat "${zip}"; then
    if [[ -d "${tmpdir}" || -f "${zipdownload}" ]]; then
      echo "Cleaning up possibly existing directories or files:"
      echo "  - ${tmpdir}"
      echo "  - ${zipdownload}"
      rm -rf "${ident}*zip"
    fi
    if mc stat "${dst}/${daisydir}" 2>/dev/null; then
      #echo "${ident} already exists at ${dst}/${daisydir}"
      #echo "Please remove for update"
      #exit 1
      echo "Removing ${dst}/${daisydir} as it already exists"
      mc rm --force --recursive "${dst}/${daisydir}"
    fi
    echo "Moving ${t} to ${SHARD}"
    mkdir "${tmpdir}_1"
    echo "Copying ${ident} to ${zipdownload}"
    mc cp "${zip}" "${zipdownload}"
    echo "done"
    echo "Unpacking ${ident} in ${tmpdir}_1"
    set +o errexit
    unzip_log="${ident}_unzip.log"
    unzip -d "${tmpdir}_1" "${zipdownload}" 2>"${unzip_log}" 1>&2
    set -o errexit
    echo "done"
    echo "Consolidating SMIL, HTML and MP3 files"
    mkdir -p "${tmpdir}_2/${daisydir}"
    find "${tmpdir}_1" -type f \( -name \*.mp3 -o -name \*.smil -o -name \*.html \) -print0 |
      xargs -r -0 -I'{}' mv '{}' "${tmpdir}_2/${daisydir}"
    echo "done"
    mandant_wbh "${ident}" "${tmpdir}_2"
    echo "Comparing number of files"
    set +o errexit
    num_files_in_zip="$(unzip -Z "${zipdownload}" | grep -cE "^(d|-).*")"
    num_extracted_files="$(find "${tmpdir}_2/${daisydir}" | wc -l)"
    if [[ "${num_files_in_zip}" != "${num_extracted_files}" ]]; then
      echo "${ident}: Number of files in ZIP (${num_files_in_zip}) differs to number of extracted files (${num_extracted_files})" |
         tee 1>>"${unzip_log}" 2>&1
    fi
    set -o errexit
    echo "done"
    echo "Moving unpack report to ausgangskorb"
    mc mv "${unzip_log}" minio/ausgangskorb
    echo "done"
    if [[ -d "${tmpdir}_2/${daisydir}" ]]; then
      pushd "${tmpdir}_2" >/dev/null
      mc mv --recursive "${daisydir}" "${dst}" |
        tee 1>>"${unzip_log}" 2>&1
      popd >/dev/null
      echo "Cleaning up temporary files"
      rm -rf "${tmpdir}_1"
      rm -rf "${tmpdir}_2"
      rm "${zipdownload}"
      echo "done"
      if mc stat "${dst}/${daisydir}" 2>/dev/null; then
        echo "Removing ${zip}"
        mc rm "${zip}"
        echo "done"
      else
        echo "Cannot stat ${dst}/${daisydir}, did not remove ${zip}" 1>>"${unzip_log}" 2>&1
      fi
    else
      echo "${ident}: Could not unzip ${zip} into ${daisydir}" 1>>"${unzip_log}" 2>&1
    fi
  else
    echo "${ident}: ${zip} not found" 1>>"${unzip_log}" 2>&1
  fi
  echo "done"
}

if [[ $# -eq 0 ]]; then
  SHARD="minio"
  TITELNUMMERN=("$(titelnummern.sh 1 100 | sort -g)")
elif [[ $# -lt 2 ]]; then
  show_usage
else
  SHARD="$1"
  shift
  TITELNUMMERN=("$@")
fi

echo "Moving ${#TITELNUMMERN[@]} audiobooks to ${SHARD}"
for t in "${TITELNUMMERN[@]}"; do
  unpack "${t}" "${SHARD}"
done

exit 0
