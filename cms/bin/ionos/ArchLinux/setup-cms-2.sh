#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

echo "Setting up physical volume and volume group 'tank'"
pvcreate /dev/sda4
vgcreate tank /dev/sda4
echo "done"

echo "Creating volume group 'swap' and filesystem"
lvcreate -L16G -n swap tank
mkswap /dev/tank/swap
echo "done"
echo "Adding swap space"
export $(blkid -o export /dev/tank/swap)
cat >>/etc/fstab <<EOF
UUID=$UUID  none  swap  defaults  0  0
EOF
unset UUID
echo "done"
echo "Activating swap space"
swapon /dev/tank/swap
echo "done"

echo "Creating volume group 'docker' and filesystem"
lvcreate -L8G -n docker tank
mkfs.ext4 /dev/tank/docker
echo "done"
echo "Mounting filesystem /var/lib/docker"
mkdir -p /var/lib/docker
export $(blkid -o export /dev/tank/docker)
cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker  ext4  rw,noatime,noexec,nodev,nosuid  0  0
EOF
unset UUID
echo "done"

echo "Creating volume group 'dockervolumes' and filesystem"
lvcreate -L500G -n dockervolumes tank
mkfs.ext4 /dev/tank/dockervolumes
echo "done"
echo "Mounting filesystem /var/lib/dockervolumes"
mkdir -p /var/lib/docker/volumes
export $(blkid -o export /dev/tank/dockervolumes)
cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker/volumes  ext4  rw,noatime,noexec,nodev,nosuid  0  0
EOF
unset UUID
echo "done"

pacinstall docker
systemctl enable docker
pacinstall docker-compose

echo "Cleaning package cache"
echo "y" | pacman -Scc
echo "done"

if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
  echo "Generating SSH key for user $(id -un)"
  ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N ""
  cat ~/.ssh/id_rsa.pub
  echo "done"
else
  echo "SSH Key id_rsa already exists"
fi

[[ -f dot_bash_profile ]] && cat dot_bash_profile >>~/.bash_profile

echo "!!!"
echo "!!!"
echo "!!! System will reboot in 10 seconds or press Ctrl-C to go back to shell"
echo "!!!"
echo "!!!"
sleep 10
systemctl reboot

exit 0
