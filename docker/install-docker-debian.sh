#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

sudo apt-key adv \
    --keyserver hkp://p80.pool.sks-keyservers.net:80 \
    --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

cat >>/etc/apt/sources.list.d/docker.list <<EOF
deb https://apt.dockerproject.org/repo debian-jessie main
EOF

sudo apt-get update \
    && sudo apt-get -y install apt-transport-https ca-certificates \
    && sudo apt-get -y install docker-engine

sudo systemctl enable docker
sudo systemctl status docker

#
# docker-compose
#
sudo curl -L \
    https://github.com/docker/compose/releases/download/1.7.0/docker-compose-`uname -s`-`uname -m` \
    >/usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

exit 0
