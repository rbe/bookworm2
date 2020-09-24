#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

pushd /var/local >/dev/null

# Generell
chown -R service:service /var/local
find /var/local -type d -print0 | xargs -r -0 chmod 770
find /var/local -type f -print0 | xargs -r -0 chmod 660
find /var/local -type f -name \*.sh -print0 | xargs -r -0 chmod 550

# App
chmod 440 /usr/local/service.jar

# Konfiguration
find /var/local -type f -name \*.yml -print0 | xargs -r -0 chmod 660
chmod 550 /var/local/conf
find /var/local/conf -type f -print0 | xargs -r -0 chmod 660

# Templates
find /var/local/templates/* -type f -print0 | xargs -r -0 chmod 660

# Daten - Hörbuchkatalog
chmod 750 /var/local/wbh/hoerbuchkatalog
# Daten - Nutzerdaten
chmod 750 /var/local/wbh/nutzerdaten
# Daten - blista DLS
chmod 550 /var/local/blista
chmod 750 /var/local/blista/dls

# Aktualisierung der Daten
chown root:root /var/local/wbh
chmod 555 /var/local/wbh
chown root:root /var/local/wbh/aktualisierung
chmod 555 /var/local/wbh/aktualisierung

# Repository
chmod 555 /var/local/repository

popd >/dev/null

exit 0
