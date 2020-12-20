#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

echo "Updating installed packages"
zypper update -y
echo "done"

exit 0
