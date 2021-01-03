#!/usr/bin/env bash
#
# Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

find /tmp/hoerbuchdienst \
  -type d \
  -mmin +30 \
  -print0 |
  xargs -r -0 rm -rf

exit 0
