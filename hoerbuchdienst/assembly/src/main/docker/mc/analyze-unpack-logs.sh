#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

for logfile in $(mc find minio/ausgangskorb --name \*.log); do
  echo "Analyzing ${logfile}"
  local_logfile="$(basename "${logfile}")"
  mc cp "${logfile}" "${local_logfile}"
  set +o errexit
  warnings=$(grep -c -i warn "${local_logfile}")
  errors=$(grep -c -i error "${local_logfile}")
  set -o errexit
  if [[ ${warnings} -gt 0 || ${errors} -gt 0 ]]; then
    echo "Found warnings and/or errors in ${logfile}"
  else
    set +o errexit
    mc mv "${logfile}" minio/ausgangskorb/log_ok/
    set -o errexit
  fi
  rm "${local_logfile}"
  echo "done"
done

exit 0
