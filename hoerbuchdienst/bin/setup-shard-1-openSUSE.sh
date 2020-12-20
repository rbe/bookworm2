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

"${execdir}"/update-linux-CentOS.sh

zypperinstall sudo
zypperinstall ca-certificates ca-certificates-cacert ca-certificates-mozilla
zypperinstall vim
zypperinstall git
zypperinstall unzip

zypperinstall logrotate
#echo "Enabling log rotation for Docker containers"
#cat >/etc/logrotate.d/docker <<EOF
#/var/lib/docker/containers/*/*.log {
#        rotate 30
#        daily
#        compress
#        missingok
#        delaycompress
#        copytruncate
#}
#EOF
#echo "done"

zypperinstall lvm2

# IONOS
if mount | grep data; then
  echo "Unmount volume 'data'"
  umount /data
fi

if vgs | grep -c hdd >/dev/null; then
  echo "Setting up volume group 'tank'"
  set +o errexit
  lvremove -f hdd data
  vgrename hdd tank
  set +o errexit
  echo "done"
fi

if grep -c "/data" /etc/fstab >/dev/null; then
  echo "Removing /data from /etc/fstab"
  grep -v "/data" /etc/fstab >fstab.$$
  mv fstab.$$ /etc/fstab
  echo "done"
fi

if ! lvs | grep -c swap >/dev/null; then
  echo "Creating volume group 'swap' and swap space"
  lvcreate -L64G -n swap vg00
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
  lvcreate -L16G -n docker tank
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
echo "!!! *** After reboot execute setup-shard-2-CentOS.sh ***"
echo "!!!"
echo "!!!"
sleep 10
systemctl reboot
