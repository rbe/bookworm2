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

pacinstall sudo
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

echo "Partitioning hard disk"
cd
sfdisk --dump /dev/sda >sda.dump.1
echo ",,L" | sfdisk --no-reread --force -a /dev/sda
sfdisk --dump /dev/sda >sda.dump.2
echo "done"
echo "Setting up physical volume and volume group 'tank'"
pvcreate /dev/sda4
vgcreate tank /dev/sda4
echo "done"

if ! lvs | grep -c swap >/dev/null; then
  echo "Creating volume group 'swap' and swap space"
  lvcreate -L16G -n swap vg00
  mkswap /dev/vg00/swap
  echo "done"
fi
if ! grep tank /proc/swaps; then
  echo "Adding swap space to fstab"
  export $(blkid -o export /dev/vg00/swap)
  cat >>/etc/fstab <<EOF
UUID=$UUID  none  swap  sw
EOF
  unset UUID
  echo "done"
  echo "Activating swap space"
  set +o errexit
  swapon -f /dev/vg00/swap
  set -o errexit
  echo "done"
fi

if ! lvs | grep -c "docker" >/dev/null; then
  echo "Creating volume group 'docker'"
  lvcreate -L8G -n docker tank
  echo "done"
fi
if ! blkid /dev/tank/docker >/dev/null; then
  echo "Creating filesystem 'docker'"
  mkfs.ext4 -F /dev/tank/docker
  echo "done"
fi
if [ ! -d /var/lib/docker ]; then
  echo "Creating mountpoint /var/lib/docker"
  mkdir -p /var/lib/docker
  echo "done"
fi
if ! grep -c "tank/docker" /etc/fstab >/dev/null; then
  echo "Adding tank/docker to fstab"
  export $(blkid -o export /dev/tank/docker)
  cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker  ext4  rw,noatime,noexec,nodev,nosuid  0  2
EOF
  unset UUID
  echo "done"
fi

if ! lvs | grep -c "dockervolumes" >/dev/null; then
  echo "Creating volume group 'dockervolumes'"
  lvcreate -y -l 90%FREE -n dockervolumes tank
  echo "done"
fi
if ! blkid /dev/tank/dockervolumes >/dev/null; then
  echo "Creating filesystem 'dockervolumes'"
  mkfs.ext4 -F /dev/tank/dockervolumes
  echo "done"
fi
if [ ! -d /var/lib/dockervolumes ]; then
  echo "Creating mountpoint /var/lib/dockervolumes"
  mkdir -p /var/lib/docker/volumes
  echo "done"
fi
if ! grep -c "tank/dockervolumes" /etc/fstab >/dev/null; then
  echo "Adding tank/dockervolumes to fstab"
  export $(blkid -o export /dev/tank/dockervolumes)
  cat >>/etc/fstab <<EOF
UUID=$UUID  /var/lib/docker/volumes  ext4  rw,noatime,noexec,nodev,nosuid  0  2
EOF
  unset UUID
  echo "done"
fi

echo "Creating users"
groupadd -f admin
if ! grep -c rbe /etc/passwd >/dev/null; then
  useradd -m -s /bin/bash -g admin rbe
fi
if ! grep -c cew /etc/passwd >/dev/null; then
  useradd -m -s /bin/bash -g admin cew
fi
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
echo "!!! *** After reboot execute setup-shard-2.sh ***"
echo "!!!"
echo "!!!"
sleep 10
systemctl reboot
