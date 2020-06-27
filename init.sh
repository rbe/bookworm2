#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ ! -d ~/.ssh && ! -f ~/.ssh/id_rsa ]]
then
    ssh-keygen -t rsa -f ~/.ssh/id_rsa -N ""
    cat ~/.ssh/id_rsa.pub
    echo ""
    echo "Please add SSH public key to Access Keys"
    exit 1
fi

ssh-keygen -R github.com
ssh-keyscan github.com 2>/dev/null 1>>~/.ssh/known_hosts
ssh-keygen -R bitbucket.org
ssh-keyscan bitbucket.org 2>/dev/null 1>>~/.ssh/known_hosts

if [[ ! -d mikrokosmos ]]
then
    git clone git@github.com:rbe/mikrokosmos.git
fi

if [[ ! -d bookworm2 ]]
then
    git clone git@bitbucket.org:artofcoding/bookworm2.git
fi

exit 0
