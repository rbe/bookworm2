#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

if [[ "$(echo PING | nc localhost 3310)" == "PONG" ]]; then
  exit 0
else
  exit 1
fi
