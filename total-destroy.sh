#!/usr/bin/env bash

set -o nounset

function failed() {
  echo "FAILED: $*"
  exit 1
}

echo "!!!"
echo "!!! ALERT: DESTROYING ALL CONATINERS AND DATA"
echo "!!!"
echo "Proceed? Enter 'yes' or use Ctrl-C to cancel."
read yn
if [[ "${yn}" == "yes" ]]; then
  if ! docker rm -f $(docker ps -aq); then
    failed "Remove all containers"
  fi
  if ! docker image ls | grep wbh- | awk '{print $1":"$2}' | xargs docker image rm -f; then
    failed "Remove all images"
  fi
  if ! docker volume prune -f; then
    failed "Removing volumes"
  fi
  if ! docker system prune -f; then
    failed "Pruning system"
  fi
fi

exit 0
