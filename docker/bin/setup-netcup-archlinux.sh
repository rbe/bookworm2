#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

if [[ $(id -un) != root ]]
then
    echo "Please execute as root"
    exit 1
fi

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
etcdir=$(pushd ${execdir}/../etc >/dev/null ; pwd ; popd >/dev/null)
platformlibdir=$(pushd ${execdir}/../../platform/src/main/bash >/dev/null ; pwd ; popd >/dev/null)
. ${platformlibdir}/archlinux.sh
. ${platformlibdir}/linux.sh
. ${platformlibdir}/ssh.sh
. ${platformlibdir}/docker.sh

function archlinux_netcup_nfs() {
    cat >>/etc/fstab <<EOF
46.38.248.210:/voln80726a1  /mnt/backup  nfs  rw,rsize=1048576,wsize=1048576  0  0
EOF
}

archlinux_update

pacman --noconfirm -S linux-lts
grub-mkconfig -o /boot/grub/grub.cfg

pacman --noconfirm -S haveged
systemctl enable haveged

pacman --noconfirm -S nfs-utils
systemctl enable rpcbind
systemctl start rpcbind

archlinux_install gnupg
archlinux_install git
archlinux_install expect

sfdisk /dev/sda <<EOF
3,,L
EOF

pvcreate /dev/sda3
vgcreate tank /dev/sda3

lvcreate --name docker -L4G tank
mkfs.ext4 /dev/tank/docker

lvcreate --name dockervolumes -L8G tank
mkfs.ext4 /dev/tank/dockervolumes

lvcreate --name wbhonline_joomla_images -L4G tank
mkfs.ext4 /dev/tank/wbhonline_joomla_images
lvcreate --name wbhonline_joomla_joomlatools_files -L4G tank
mkfs.ext4 /dev/tank/wbhonline_joomla_joomlatools_files

cat >>/etc/fstab <<EOF
/dev/tank/docker         /var/lib/docker          ext4  rw,noatime  0  2
/dev/tank/dockervolumes  /var/lib/docker/volumes  ext4  rw,noatime  0  2
/dev/tank/wbhonline_joomla_images             /var/lib/docker/volumes/wbhonline_joomla_images             ext4  rw,noatime  0  2
/dev/tank/wbhonline_joomla_joomlatools_files  /var/lib/docker/volumes/wbhonline_joomla_joomlatools_files  ext4  rw,noatime  0  2
EOF
mkdir /var/lib/docker
chmod 711 /var/lib/docker
mkdir /var/lib/docker/volumes
chmod 711 /var/lib/docker/volumes
mkdir /var/lib/docker/volumes/wbhonline_joomla_images
mkdir /var/lib/docker/volumes/wbhonline_joomla_joomlatools_files
mount -a

sed -i'' \
    -e "s#^HOOKS=.*#HOOKS=(base udev block autodetect modconf lvm2 filesystems keyboard fsck)#" \
    /etc/mkinitcpio.conf
mkinitcpio -p linux

archlinux_install_docker
sudo sysctl net.ipv4.conf.all.forwarding=1
sudo iptables -P FORWARD ACCEPT

archlinux_netcup_nfs

groupadd -g 4801 bookworm

setup_user_w_sudo rbe
ssh_setup_key rbe $(cat etc/authorized_keys rbe)
sudo usermod -g bookworm -aG docker rbe

setup_user_w_sudo cew
ssh_setup_key cew $(cat etc/authorized_keys cew)
sudo usermod -g bookworm -aG docker cew

exit 0
