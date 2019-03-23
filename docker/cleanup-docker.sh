#!/usr/bin/env bash

sudo docker rm $(sudo docker ps -qa)
sudo docker volume prune -f
sudo docker image rm wbh/datatransfer:1
sudo docker image rm wbh/rproxy:1
sudo docker image rm wbh/hoerbuchkatalog:1
sudo docker image prune -f

exit 0
