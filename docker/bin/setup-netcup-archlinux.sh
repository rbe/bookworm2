#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
. ${platformlibdir}/archlinux.sh
. ${platformlibdir}/linux.sh
. ${platformlibdir}/ssh.sh
. ${platformlibdir}/docker.sh

function archlinux_netcup_nfs() {
    sudo pacman --noconfirm -S nfs-utils
    sudo systemctl enable rpcbind
    sudo systemctl start rpcbind
    sudo bash -c 'cat >>/etc/fstab <<EOF
46.38.248.210:/voln80726a1	/mnt/backup	nfs	rw,rsize=1048576,wsize=1048576	0	0
EOF'
}

archlinux_update

sudo pacman --noconfirm -S linux-lts
sudo grub-mkconfig -o /boot/grub/grub.cfg

sudo pacman --noconfirm -S haveged
sudo systemctl enable haveged

sfdisk /dev/sda <<EOF
3,,L
EOF
pvcreate /dev/sda3
vgcreate tank /dev/sda3
lvcreate --name docker -L4G tank
mkfs.ext4 /dev/tank/docker
lvcreate --name dockervolumes -L10G tank
mkfs.ext4 /dev/tank/dockervolumes
cat >>/etc/fstab <<EOF
/dev/tank/docker         /var/lib/docker          ext4  rw,noatime  0  2
/dev/tank/dockervolumes  /var/lib/docker/volumes  ext4  rw,noatime  0  2
EOF
sed -i'' \
    -e "s#^HOOKS=.*#HOOKS=(base udev block autodetect modconf lvm2 filesystems keyboard fsck)#" \
    /etc/mkinitcpio.conf
mkinitcpio -p linux
mkdir /var/lib/docker
chmod 711 /var/lib/docker
mkdir /var/lib/docker/volumes
chmod 711 /var/lib/docker

archlinux_install_docker
sudo sysctl net.ipv4.conf.all.forwarding=1
sudo iptables -P FORWARD ACCEPT

archlinux_install expect git

archlinux_netcup_nfs

setup_user_w_sudo rbe
ssh_setup_key rbe $(cat etc/authorized_keys rbe)
sudo usermod -aG docker rbe

setup_user_w_sudo cew
ssh_setup_key cew $(cat etc/authorized_keys cew)
sudo usermod -aG docker cew

exit 0
