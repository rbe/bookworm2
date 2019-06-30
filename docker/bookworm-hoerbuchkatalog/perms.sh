#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

cd /opt/bookworm

# App
chown bookworm:bookworm *.sh
chmod 555 .
chmod 550 *.sh
chown bookworm:bookworm app/*
chmod 550 app
chmod 660 app/*
# Konfiguration
chown bookworm:bookworm conf/*
chmod 660 *.yml
chmod 440 conf/*
chmod 660 conf/secrets.json
chmod 660 conf/hoerbuchkatalog.properties
chmod 660 conf/blista-dls.properties

# Daten - Hörbuchkatalog
chown -R bookworm:bookworm var/wbh/hoerbuchkatalog
find var/wbh/hoerbuchkatalog -type d -print0 | xargs -0 chmod 770
find var/wbh/hoerbuchkatalog -type f -print0 | xargs -0 chmod 660
chmod 750 var/wbh/hoerbuchkatalog

# Daten - Nutzerdaten
chown -R bookworm:bookworm var/wbh/nutzerdaten
find var/wbh/nutzerdaten -type d -print0 | xargs -0 chmod 770
find var/wbh/nutzerdaten -type f -print0 | xargs -0 chmod 660
chmod 750 var/wbh/nutzerdaten

# Aktualisierung der Daten
chown root:root var
chown root:root var/wbh
chown root:root var/wbh/aktualisierung
chmod 555 var
chmod 555 var/wbh
chmod 555 var/wbh/aktualisierung
# Aktualisierung der Daten - Eingangskorb
chown bookworm:bookworm var/wbh/aktualisierung/eingangskorb
chmod 770 var/wbh/aktualisierung/eingangskorb
# Aktualisierung der Daten - Ausgangskorb
chmod 770 var/wbh/aktualisierung/ausgangskorb
chown bookworm:bookworm var/wbh/aktualisierung/ausgangskorb

# blista DLS
chmod 550 var/blista
chmod 750 var/blista/dls

# Repository
find var/repository -type d -print0 | xargs -0 chmod 770
find var/repository -type f -print0 | xargs -0 chmod 660
chmod 555 var/repository

# var
chown root:root var/*
chmod 555 var/*

exit 0
