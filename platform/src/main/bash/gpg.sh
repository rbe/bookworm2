#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

function gpg_gen_key() {
    local email=$1 ; shift
    local username=$*
    local passphrase=$(dd if=/dev/random bs=512 count=4 2>/dev/null | tr -cd '[:alnum:]' | cut -b 1-32)
    local tmp=tmp.$$
    cat >${tmp} <<EOF
%echo Generating an OpenPGP key
Key-Type: RSA
Key-Length: 4096
Subkey-Type: RSA
Subkey-Length: 2048
Name-Real: "${username}"
Name-Email: ${email}
Expire-Date: 0
Passphrase: ${passphrase}
%commit
%echo done
EOF
    gpg --full-gen-key --batch ${tmp}
    rm ${tmp}
    gpg --list-secret-keys
    echo "Passphrase is '${passphrase}'"
}

function gpg_export_privkey() {
    local username=$*
    local filename=${username/ /_}
    gpg --export-secret-keys --armor "${username}" > ${filename}_pub.asc
}

function gpg_export_pubkey() {
    local username=$*
    local filename=${username/ /_}
    gpg --export --armor "${username}" > ${filename}_pub.asc
}

gpg_gen_key rbe@medienhof10 rbe-medienhof10
