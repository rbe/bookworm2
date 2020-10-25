#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $(id -un) == 0 ]]; then
  echo "Please don't execute as root"
  exit 1
fi

git clone https://aur.archlinux.org/trivy-bin.git
pushd trivy-bin 2>/dev/null
makepkg -si
popd 2>/dev/null

exit 0
