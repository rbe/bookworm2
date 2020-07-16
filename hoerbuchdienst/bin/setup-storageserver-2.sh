#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

echo "Setting up physical volume and volume group 'tank'"
pvcreate /dev/sda4
vgcreate tank /dev/sda4
echo "done"

echo "Creating volume group 'docker' and filesystem"
lvcreate -L8G -n docker tank
mkfs.ext4 /dev/tank/docker
echo "done"
echo "Mounting filesystem /var/lib/docker"
mkdir /var/lib/docker
export $(blkid -o export /dev/tank/docker)
cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker  ext4  rw,noatime,noexec,nodev,nosuid  0  0
EOF
unset UUID
echo "done"

echo "Creating volume group 'dockervolumes' and filesystem"
lvcreate -L4.5T -n dockervolumes tank
mkfs.ext4 /dev/tank/dockervolumes
echo "done"
echo "Mounting filesystem /var/lib/dockervolumes"
export $(blkid -o export /dev/tank/dockervolumes)
cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker/volumes  ext4  rw,noatime,noexec,nodev,nosuid  0  0
EOF
unset UUID
echo "done"

echo "!!!"
echo "!!!"
echo "!!! System will reboot in 10 seconds or press Ctrl-C to go back to shell"
echo "!!!"
echo "!!!"
sleep 10
systemctl reboot

exit 0
