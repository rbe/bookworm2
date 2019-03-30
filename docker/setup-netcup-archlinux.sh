#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
libdir=$(pushd ${execdir}/lib >/dev/null ; pwd ; popd >/dev/null)
. ${libdir}/archlinux.sh
. ${libdir}/linux.sh
. ${libdir}/ssh.sh
. ${libdir}/docker.sh

function archlinux_netcup_nfs() {
    sudo pacman --noconfirm -S nfs-utils
    sudo systemctl enable rpcbind
    sudo systemctl start rpcbind
    sudo bash -c 'cat >>/etc/fstab <<EOF
46.38.248.210:/voln80726a1	/mnt/backup	nfs	rw,rsize=1048576,wsize=1048576	0	0
EOF'
}

archlinux_update
archlinux_install_docker
archlinux_install expect git jre-openjdk
archlinux_netcup_nfs

setup_user_w_sudo rbe
ssh_setup_key rbe $(cat etc/authorized_keys rbe)
setup_user_w_sudo cew
ssh_setup_key cew $(cat etc/authorized_keys cew)

exit 0
