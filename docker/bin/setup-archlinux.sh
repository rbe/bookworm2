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

#
# Linux Packages
#

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

#
# Storage
#

sfdisk /dev/sda <<EOF
3,,L
EOF
# Volume Group "tank"
pvcreate /dev/sda3
vgcreate tank /dev/sda3
# Docker (/var/lib/docker)
lvcreate --name docker -L4G tank
mkfs.ext4 /dev/tank/docker
# Docker Volumes (/var/lib/docker/volumes)
lvcreate --name dockervolumes -L16G tank
mkfs.ext4 /dev/tank/dockervolumes
# Docker Backup (/var/lib/docker/backup)
lvcreate --name dockerbackup -L16G tank
mkfs.ext4 /dev/tank/dockerbackup
# fstab
cat >>/etc/fstab <<EOF
/dev/tank/docker         /var/lib/docker          ext4  rw,noatime  0  2
/dev/tank/dockervolumes  /var/lib/docker/volumes  ext4  rw,noatime  0  2
/dev/tank/dockerbackup   /var/lib/docker/backup   ext4  rw,noatime  0  2
EOF
mkdir /var/lib/docker
chmod 711 /var/lib/docker
mkdir /var/lib/docker/volumes
chmod 711 /var/lib/docker/volumes
mkdir /var/lib/docker/backup
chmod 750 /var/lib/docker/backup
mount -a
# Install Docker
archlinux_install_docker
# Docker outgoing connections
sudo sysctl net.ipv4.conf.all.forwarding=1
sudo iptables -P FORWARD ACCEPT
# Docker Logfiles
cat >/etc/logrotate.d/docker <<EOF
/var/lib/docker/containers/*/*.log {
        rotate 30
        daily
        compress
        missingok
        delaycompress
        copytruncate
}
EOF

#
# Linux
#

# Linux Kernel
sed -i'' \
    -e "s#^HOOKS=.*#HOOKS=(base udev block autodetect modconf lvm2 filesystems keyboard fsck)#" \
    /etc/mkinitcpio.conf
mkinitcpio -p linux

exit 0
