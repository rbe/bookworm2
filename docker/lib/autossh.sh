#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

execdir=$(pushd `dirname $0` >/dev/null ; pwd ; popd >/dev/null)
libdir=$(pushd ${execdir}/lib >/dev/null ; pwd ; popd >/dev/null)
. ${libdir}/ssh.sh
. ${libdir}/git.sh

keyfile=~/.ssh/id_rsa
secretfile=${keyfile}.secret
ssh_create_key_ifmissing ${keyfile} ${secretfile}
ssh_auto_agent ${keyfile} ${secretfile}

git_add_sshconfig_for bitbucket.org
ssh_scan_key bitbucket.org
