#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function pacinstall() {
  local packages="$*"
  shift
  echo "Installing ${packages}"
  if pacman --noconfirm -S "${packages}"; then
    echo "${packages} installed successfully"
  else
    echo "${packages} installation failed"
  fi
}
