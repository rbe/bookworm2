#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

echo "Updating ArchLinux keyring"
pacman --noconfirm -Sy archlinux-keyring
echo "Updating installed packages"
pacman --noconfirm -Syu
echo "done"

exit 0
