#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $(id -un) != root ]]
then
    echo "Please execute as root"
    exit 1
fi

execdir=$(pushd "$(dirname $0)" >/dev/null || exit 1; pwd ; popd >/dev/null || exit 1)
platformlibdir=$(pushd "${execdir}"/../../platform/src/main/bash >/dev/null || exit 1; pwd ; popd >/dev/null || exit 1)
. "${platformlibdir}/linux.sh"
. "${platformlibdir}/ssh.sh"

groupadd -g 4801 bookworm

setup_user_w_sudo rbe
ssh_setup_key rbe "$(cat etc/authorized_keys rbe)"
sudo usermod -g bookworm -aG docker rbe

setup_user_w_sudo cew
ssh_setup_key cew "$(cat etc/authorized_keys cew)"
sudo usermod -g bookworm -aG docker cew

exit 0
