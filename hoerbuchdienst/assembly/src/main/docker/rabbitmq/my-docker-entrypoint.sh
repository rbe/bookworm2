#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

echo "Starting crond to regularly export definitions"
echo "" | crontab -
crond -b -S -l 8
echo "done"

exec docker-entrypoint.sh
