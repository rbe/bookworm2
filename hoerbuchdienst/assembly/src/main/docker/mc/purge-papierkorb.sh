#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

echo "!!!"
echo "!!! Purging 'papierkorb'"
echo "!!!"
mc rm --force --recursive minio/papierkorb

exit 0
