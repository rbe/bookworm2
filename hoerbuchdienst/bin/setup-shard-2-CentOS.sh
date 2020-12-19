#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

yuminstall docker
yuminstall docker-logrotate
yuminstall docker-compose
systemctl enable docker

echo "Cleaning package cache"
yum -y clean

if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
  echo "Generating SSH key for user $(id -un)"
  ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N "" -C "$(hostname -f)"
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
