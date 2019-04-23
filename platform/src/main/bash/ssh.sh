#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

function ssh_setup_key() {
    local name=$1
    local pubkey=$2
    mkdir /home/${name}/.ssh
    echo "${pubkey}" >>/home/${name}/.ssh/authorized_keys
    chown -R ${name}:${name} /home/${name}
    chmod 700 /home/${name}/.ssh
    chmod 400 /home/${name}/.ssh/*
    chmod 444 /home/${name}/.ssh/*.pub
}

function ssh_setup_key_ifmissing() {
    local keyfile=$1
    local secretfile=$2
    if [[ ! -f ${keyfile} || ! -f ${secretfile} ]]
    then
        ssh_create_key ${keyfile} ${secretfile}
    fi
}

function ssh_create_key() {
    local keyfile=$1
    local secretfile=$2
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
}

function ssh_scan_key() {
    local server=$1
    [[ -f ~/.ssh/known_hosts ]] && chmod 600 ~/.ssh/known_hosts
    ssh-keygen -R ${server}
    ssh-keyscan -46 -t rsa ${server} >>~/.ssh/known_hosts
}

function ssh_auto_agent() {
    local keyfile=$1
    local secretfile=$2
    eval $(ssh-agent)
    if [[ $(grep -c "kill \${SSH_AGENT_PID}" ${HOME}/.bash_logout) = 0 ]]
    then
        cat >>${HOME}/.bash_logout <<EOF
kill \${SSH_AGENT_PID}
EOF
    fi
    expect >/dev/null <<EOF
  spawn ssh-add ${keyfile}
  expect "Enter passphrase"
  set pass [read [open "${secretfile}" r]]
  send "\$pass\r"
  expect eof
EOF
}
