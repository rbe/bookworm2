#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

pushd ~/bookworm2 >/dev/null
git fetch origin
git checkout origin/master -- autoupdate.sh

autoupdate.sh prod hbd force

cnt.sh prod hbd exec hbd-rproxy \
  provision.sh -e site \
    default_tls_server \
    minio \
    hoerbuchdienst

popd >/dev/null

exit 0
