#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

function setup_user_w_sudo() {
    local name=$1
    getent passwd ${name}
    if [[ $? -eq 2 ]]
    then
        groupadd ${name}
        useradd -m -d /home/${name} -s /bin/bash -g ${name} ${name}
    fi
    cat >> /etc/sudoers.d/${name} <<EOF
${name} ALL=(ALL) NOPASSWD: ALL
EOF
}
