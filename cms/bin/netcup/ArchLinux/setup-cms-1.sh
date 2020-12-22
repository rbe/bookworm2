#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

set_fqdn

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

echo "Cleaning package cache"
echo "y" | pacman -Scc
echo "done"

echo "Setting timezone"
timedatectl set-timezone Europe/Berlin
echo "done"
echo "Enabling NTP"
timedatectl set-ntp true
echo "done"

pacinstall lvm2

echo "Partitioning hard disk"
cd
sfdisk --dump /dev/sda >sda.dump.1
echo ",,L" | sfdisk --no-reread --force -a /dev/sda
sfdisk --dump /dev/sda >sda.dump.2
echo "done"

echo "!!!"
echo "!!!"
echo "!!! System will reboot in 10 seconds or press Ctrl-C to go back to shell"
echo "!!!"
echo "!!!"
echo "!!! *** After reboot execute setup-cms-2.sh ***"
echo "!!!"
echo "!!!"
sleep 10
systemctl reboot
