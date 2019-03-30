#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

function git_add_sshconfig_for() {
    local hostname=$1
    if [[ $(grep -Ec "^[Hh]ost ${host}" ~/.ssh/config) = 0 ]]
    then
        cat >>~/.ssh/config <<EOF
Host ${hostname}
    HostName ${hostname}
    IdentityFile ~/.ssh/id_rsa
    User git
EOF
    fi
}
