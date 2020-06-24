#!/usr/bin/env ash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

PASSWORD=$1

cd /usr/local/tls-gen/basic
make PASSWORD=${PASSWORD} DAYS_OF_VALIDITY=3650
make verify
make info
ls -l ./result

exit 0
