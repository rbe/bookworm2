#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

chmod 500 $0

keyfile=~/.ssh/id_rsa
secretfile=${keyfile}.secret
if [[ ! -f ${keyfile} || ! -f ${secretfile} ]]
then
    rm -f ${secretfile}
    dd if=/dev/urandom bs=512 count=4 \
        | tr -cd '[:alnum:]' \
        | cut -b 1-32 \
        >${secretfile}
    chmod 400 ${secretfile}
    rm -f ${keyfile}
    ssh-keygen -t rsa -b 4096 \
        -P"$(cat ${secretfile})" -N"$(cat ${secretfile})" \
        -f ${keyfile}
    chmod 400 ${keyfile}
fi

sudo pacman -Qi expect >/dev/null
if [[ $? = 1 ]]
then
    sudo pacman --noconfirm -S expect
fi

eval $(ssh-agent)
expect >/dev/null <<EOF
  spawn ssh-add ${keyfile}
  expect "Enter passphrase"
  set pass [read [open "${secretfile}" r]]
  send "\$pass\r"
  expect eof
EOF

if [[ $(grep -Ec "^[Hh]ost bitbucket.org" ~/.ssh/config) = 0 ]]
then
    cat >>~/.ssh/config <<EOF
Host bitbucket.org
    HostName bitbucket.org
    IdentityFile ~/.ssh/id_rsa
    User git
EOF
fi

[[ -f ~/.ssh/known_hosts ]] && chmod 600 ~/.ssh/known_hosts
ssh-keyscan -46 -t rsa bitbucket.org >>~/.ssh/known_hosts
