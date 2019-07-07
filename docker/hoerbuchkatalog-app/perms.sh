#!/bin/sh
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

cd /opt/bookworm

# Generell
chown -R bookworm:bookworm .
find . -type d -print0 | xargs -0 chmod 770
find . -type f -print0 | xargs -0 chmod 660

# App
chmod 555 .
chmod 550 bin/*.sh
chmod 550 app
chmod 660 app/*

# Konfiguration
chmod 660 *.yml
chmod 440 conf/*
chmod 660 conf/secrets.json
chmod 660 conf/hoerbuchkatalog.properties
chmod 660 conf/blista-dls.properties

# Daten - Hörbuchkatalog
chmod 750 var/wbh/hoerbuchkatalog
# Daten - Nutzerdaten
chmod 750 var/wbh/nutzerdaten
# Aktualisierung der Daten
chown root:root var
chmod 555 var
chown root:root var/wbh
chmod 555 var/wbh
chown root:root var/wbh/aktualisierung
chmod 555 var/wbh/aktualisierung

# blista DLS
chmod 550 var/blista
chmod 750 var/blista/dls

# Repository
chmod 555 var/repository

# var
chown root:root var/*
chmod 555 var/*

exit 0
