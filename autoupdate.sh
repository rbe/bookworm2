#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <env> <project>"
  echo "  env        dev | prod"
  echo "  project    hbk | hbd"
  exit 1
fi
env=$1
shift
project=$1

echo "$(time) Starting update"

pushd ~/bookworm2 >/dev/null

echo "Fetching changes from origin"
git fetch origin
echo "done"

revlist_count=$(git rev-list --pretty=oneline develop..origin/develop | wc -l)
#log_count=$(git log --pretty=oneline develop..origin/develop | wc -l)
#diff_count=$(git diff --stat origin/develop.. | wc -l)
echo "Found ${revlist_count} changes"

if [[ ${revlist_count} -gt 0 ]]; then
  echo "!!!"
  echo "!!! Building ${env}-${project}"
  echo "!!!"
  ./build.sh "${env}"
  echo "!!!"
  echo "!!! Deploying ${env}-${project}"
  echo "!!!"
  ./deploy.sh "${env}" "${project}"
  echo "!!!"
  echo "!!! Restarting ${env}-${project}"
  echo "!!!"
  ./restart.sh "${env}" "${project}"
  echo "!!!"
  echo "!!! Cleaning up ${env} hbk"
  echo "!!!"
  ./cleanup.sh "${env}" hbk
  echo "!!!"
  echo "!!! Cleaning up ${env} hbd"
  echo "!!!"
  ./cleanup.sh "${env}" hbd
  echo "done"
else
  echo "No updates found, doing nothing"
fi

popd >/dev/null

echo "$(time) Update ended successfully"

exit 0