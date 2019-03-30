#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

function archlinux_update() {
    sudo pacman --noconfirm -Syu
}

function archlinux_install() {
    local pkgs=$*
    sudo pacman --noconfirm -S ${pkgs}
}

function archlinux_install_check() {
    local pkgs=$*
    for pkg in ${pkgs}
    do
        sudo pacman -Qi ${pkg} 2>&1 >/dev/null
        if [[ $? = 1 ]]
        then
            sudo pacman --noconfirm -S ${pkg}
        fi
    done
}
