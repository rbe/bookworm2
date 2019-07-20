#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

NETCUP_HOST=46.38.248.210
NETCUP_VOLUME=/voln80726a1
BACKUP_DIR=/mnt/backup

set -o nounset

if [[ $(id -un) != root ]]
then
    echo "Please execute as root"
    exit 1
fi

cat >>/etc/fstab <<EOF
${NETCUP_HOST}:${NETCUP_VOLUME}  ${BACKUP_DIR}  nfs  rw,rsize=1048576,wsize=1048576  0  0
EOF

if [[ ! -d ${BACKUP_DIR} ]]
then
    mkdir -p ${BACKUP_DIR}
fi

exit 0
