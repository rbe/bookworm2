#!/bin/ash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

alpinelinux_setup_user() {
  local name=$1
  echo "Trying to add ${name}"
  if ! getent passwd "${name}" >/dev/null; then
    echo "Adding user ${name}"
    adduser -DHh /tmp -s /bin/false -G service "${name}"
    echo "done"
  fi
  echo "Setting password for ${name}"
  passwd -u "${name}"
  echo "done"
}

if [[ $# -eq 0 ]]; then
  echo "usage: $0 user1[ user2 ... userN]"
  exit 1
fi

for user in "$@"; do
  alpinelinux_setup_user "${user}"
done

exit 0
