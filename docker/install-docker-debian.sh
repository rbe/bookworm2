#!/usr/bin/env bash

# Add Docker GPG key.
sudo apt-key adv \
    --keyserver hkp://p80.pool.sks-keyservers.net:80 \
    --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

# Create a new source file under /etc/apt/sources.list.d/.
sudo nano /etc/apt/sources.list.d/docker.list

# Add this line in the file and save the file.
deb https://apt.dockerproject.org/repo debian-jessie main

# Because the docker repository requires HTTPS connection
# so we need to install apt-transport-https and ca-certificates package
# to make APT establish HTTPS connection with docker repository.
sudo apt-get install apt-transport-https ca-certificates

# Update local package index and install docker on Debian 8.
sudo apt-get update && sudo apt-get install docker-engine

sudo systemctl enable docker
sudo systemctl status docker

#
# docker-compose
#

su -
curl -L https://github.com/docker/compose/releases/download/1.7.0/docker-compose-`uname -s`-`uname -m` \
    >/usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
