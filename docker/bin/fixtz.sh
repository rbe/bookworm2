#!/usr/bin/env bash

TZ="Europe/Berlin"

apk --no-cache add tzdata
cp /usr/share/zoneinfo/${TZ} /etc/localtime
echo "${TZ}" >/etc/timezone
apk del tzdata
