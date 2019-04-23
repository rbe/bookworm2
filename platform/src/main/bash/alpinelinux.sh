#!/bin/ash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

function alpinelinux_setup_user() {
    local name=$1
    shift
    local groups=$*
    getent passwd ${name}
    if [[ -n "${name}" ]]
    then
        echo "Adding user ${name}"
        adduser -DHh /tmp -s /bin/false -G bookworm ${name}
    fi
    passwd -u ${name}
    if [[ -n "${groups}" ]]
    then
        for g in ${groups}
        do
            echo "Adding user ${name} to group ${g}"
            usermod -G ${g} ${name}
        done
    fi
}
