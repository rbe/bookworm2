#!/usr/bin/env bash
#
# Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

sudo docker rm -f $(sudo docker ps -qa)
sudo docker volume prune -f
sudo docker network prune -f

sudo docker image rm wbh/datatransfer:1
sudo docker image rm wbh/rproxy:1
sudo docker image rm wbh/hoerbuchkatalog:1
sudo docker image prune -f

exit 0
