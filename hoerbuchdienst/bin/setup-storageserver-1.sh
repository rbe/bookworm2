#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

set_fqdn

echo "Setting timezone"
timedatectl set-timezone Europe/Berlin
echo "done"
echo "Enabling NTP"
timedatectl set-ntp true
echo "done"

"${execdir}"/update-linux.sh

pacinstall inetutils
pacinstall man man-pages
pacinstall pacman-contrib
echo "Enable paccache timer"
systemctl enable paccache.timer
echo "done"
pacinstall ca-certificates ca-certificates-mozilla ca-certificates-utils
pacinstall vi vim
pacinstall git
pacinstall unzip

pacinstall logrotate
echo "Enabling logrotate timer"
systemctl enable logrotate.timer
echo "done"
echo "Enabling log rotation for Docker containers"
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
echo "done"

pacinstall lvm2

# netcup
#echo "Partitioning hard disk"
#cd
#sfdisk --dump /dev/sda >sda.dump.1
#echo ",,L" | sfdisk --no-reread --force -a /dev/sda
#sfdisk --dump /dev/sda >sda.dump.2
#echo "done"
#echo "Setting up physical volume and volume group 'tank'"
#pvcreate /dev/sda4
#vgcreate tank /dev/sda4
#echo "done"

# IONOS
echo "Setting up volume group 'tank'"
set +o errexit
umount /data
lvremove -f hdd data
vgrename hdd tank
set -o errexit
grep -v "/data" /etc/fstab >fstab.$$
mv fstab.$$ /etc/fstab
echo "done"

echo "Creating volume group 'swap' and swap space"
lvcreate -L64G -n swap vg00
mkswap /dev/vg00/swap
echo "done"
echo "Adding swap space"
export $(blkid -o export /dev/tank/swap)
cat >>/etc/fstab <<EOF
UUID=$UUID  none  swap  defaults  0  0
EOF
unset UUID
echo "done"
echo "Activating swap space"
swapon -f /dev/vg00/swap
echo "done"

echo "Creating volume group 'docker' and filesystem"
lvcreate -L16G -n docker tank
mkfs.ext4 -f /dev/tank/docker
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
lvcreate -y -l 90%FREE -n dockervolumes tank
mkfs.ext4 -f /dev/tank/dockervolumes
echo "done"
echo "Mounting filesystem /var/lib/dockervolumes"
mkdir -p /var/lib/docker/volumes
export $(blkid -o export /dev/tank/dockervolumes)
cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker/volumes  ext4  rw,noatime,noexec,nodev,nosuid  0  0
EOF
unset UUID
echo "done"

echo "Creating users"
groupadd admin
useradd -m -s /bin/bash -g admin rbe
useradd -m -s /bin/bash -g admin cew
cat >/etc/sudoers.d/admin <<EOF
rbe ALL=(ALL) NOPASSWD: ALL
cew ALL=(ALL) ALL
EOF
echo "done"

echo "***"
echo "*** Please check fstab"
echo "***"
cat /etc/fstab
echo "***"
echo "***"
read

echo "!!!"
echo "!!!"
echo "!!! System will reboot in 10 seconds or press Ctrl-C to go back to shell"
echo "!!!"
echo "!!!"
echo "!!! *** After reboot execute setup-storageserver-2.sh ***"
echo "!!!"
echo "!!!"
sleep 10
systemctl reboot
