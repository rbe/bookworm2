#!/usr/bin/env bash
#
# Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# -lt 2 ]]; then
  echo "usage: $0 <env> <project> [<force>]"
  echo "  env        dev | prod"
  echo "  project    hbk | hbd"
  echo "  force      force build"
  exit 1
fi
env="$1"
shift
project="$1"
if [[ $# == 2 ]]; then
  shift
  force="${1:-}"
  echo "${force} mode"
else
  force=""
fi

execdir="$(
  pushd "$(dirname "$0")" >/dev/null
  pwd
  popd   >/dev/null
)"

echo "$(date) Starting update"

#
# Mikrokosmos
#

if [[ ! -d "${execdir}"/../mikrokosmos ]]; then
  echo "Cloning Mikrokosmos"
  mkdir -p "${execdir}"/../mikrokosmos
  pushd "${execdir}"/../mikrokosmos >/dev/null
  git clone git@github.com:rbe/mikrokosmos.git .
  git config pull.rebase false
  git checkout master
  popd >/dev/null
else
  echo "Updating Mikrokosmos, fetching changes from origin"
  pushd "${execdir}"/../mikrokosmos >/dev/null
  git reset --hard && git fetch origin
  git checkout master
  revlist_count=$(git rev-list --pretty=oneline master..origin/master | wc -l)
  echo "Found ${revlist_count} changes"
  if [[ ${revlist_count} -gt 0 ]]; then
    echo "***"
    echo "*** Pulling changes"
    echo "***"
    if ! git pull origin; then
      echo "!!!"
      echo "!!! Error pulling changes, exiting"
      echo "!!!"
      exit 1
    fi
  fi
  popd >/dev/null
fi
echo "done"

#
# WBH Bookworm
#

pushd "${execdir}" >/dev/null

echo "Updating WBH Bookworm, fetching changes from origin"
git reset --hard && git fetch origin
echo "done"
current_branch="$(git branch --show-current)"
if [[ "${current_branch}" != "master" ]]; then
  echo "Switching branch to master"
  git checkout master
  echo "done"
fi

revlist_count=$(git rev-list --pretty=oneline master..origin/master | wc -l)
#log_count=$(git log --pretty=oneline master..origin/master | wc -l)
#diff_count=$(git diff --stat origin/master.. | wc -l)
echo "Found ${revlist_count} changes"

if [[ ${revlist_count} -gt 0 || -n "${force}" ]]; then
  echo "***"
  echo "*** Pulling changes"
  echo "***"
  if ! git pull origin; then
    echo "!!!"
    echo "!!! Error pulling changes, exiting"
    echo "!!!"
    exit 1
  fi
  echo "done"
  echo "***"
  echo "*** Building ${env}-${project}"
  echo "***"
  if ! ./build.sh "${env}"; then
    echo "!!!"
    echo "!!! Error building project, exiting"
    echo "!!!"
    exit 1
  fi
  echo "***"
  echo "*** Deploying ${env}-${project}"
  echo "***"
  if ! ./deploy.sh "${env}" "${project}"; then
    echo "!!!"
    echo "!!! Error deploying project, exiting"
    echo "!!!"
    exit 1
  fi
  echo "***"
  echo "*** Restarting ${env}-${project}"
  echo "***"
  ./restart.sh "${env}" "${project}"
  echo "***"
  echo "*** Cleaning up ${env} hbk"
  echo "***"
  ./cleanup.sh "${env}" hbk
  echo "***"
  echo "*** Cleaning up ${env} hbd"
  echo "***"
  ./cleanup.sh "${env}" hbd
  echo "done"
else
  echo "No updates found, doing nothing"
fi

popd >/dev/null

echo "$(date) Update ended successfully"

exit 0
