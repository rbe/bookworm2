#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

if [[ ! -f "/data/main.cvd" ]]; then
  echo   "Initial download of virus databases"
  freshclam   -v
fi

echo "Starting the update daemon"
freshclam -l /proc/self/fd/1 -p /var/run/freshclam.pid -d -c 12

echo "Starting ClamAV in foreground"
clamd
