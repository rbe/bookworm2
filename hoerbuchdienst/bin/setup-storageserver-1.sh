#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

correct=0
while [[ ${correct} != 1 ]]; do
  read -r -e -p "Please enter FQDN of shard: " shard_fqdn
  if [[ -n "${shard_fqdn}" ]]; then
    shard_name="${shard_fqdn/.*/}"
    shard_domain="${shard_fqdn#*.}"
    echo "Shard name is ${shard_name}"
    echo "Shard's domain is ${shard_domain}"
  fi
  if [[ -n "${shard_fqdn}" ]]; then
    read -r -e -p "Is this correct? (Enter 'yes') " correct_answer
    [[ "${correct_answer}" == "yes" ]] && correct=1
    echo -e "${shard_fqdn}" >/etc/hostname
    hostnamectl set-hostname "$(cat /etc/hostname)"
  else
    echo "No FQDN entered!"
    correct=0
  fi
done

echo "Updating ArchLinux keyring"
pacman --noconfirm -Sy archlinux-keyring
echo "Updating installed packages"
pacman --noconfirm -Syu
echo "done"

pacinstall inetutils
pacinstall man man-pages
pacinstall pacman-contrib
echo "Enable paccache timer"
systemctl enable paccache.timer
echo "done"
pacinstall ca-certificates ca-certificates-mozilla ca-certificates-utils
pacinstall vi vim
pacinstall git

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

echo "Cleaning package cache"
pacman --noconfirm -Scc
echo "done"

echo "Setting timezone"
timedatectl set-timezone Europe/Berlin
echo "done"
echo "Enabling NTP"
timedatectl set-ntp true
echo "done"

echo "Partitioning hard disk"
sfdisk --dump /dev/sda >sda.dump.1
echo ",,30" | sfdisk --force -a /dev/sda
sfdisk --dump /dev/sda >sda.dump.2
echo "done"

pacinstall lvm2

echo "!!!"
echo "!!!"
echo "!!! System will reboot in 10 seconds or press Ctrl-C to go back to shell"
echo "!!!"
echo "!!!"
#echo "!!! *** After reboot execute setup-storageserver-2.sh ***"
#echo "!!!"
#echo "!!!"
sleep 10
systemctl reboot
