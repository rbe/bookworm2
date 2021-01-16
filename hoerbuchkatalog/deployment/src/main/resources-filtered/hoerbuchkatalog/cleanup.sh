#!/usr/bin/env bash
#
# Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

function remove_older_than_days {
  local dir="$1"
  local days="$2"
  find "${dir}" \
    -type f -mindepth 1 -maxdepth 1 \
    -mtime +${days} \
    -print0 |
    xargs -r -0 rm
}

remove_older_than_days /var/local/repository/Session 5
remove_older_than_days /var/local/repository/Warenkorb 5
remove_older_than_days /var/local/repository/Bestellung 5

exit 0
