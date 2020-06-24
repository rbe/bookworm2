#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset

mkdir -p /opt/bookworm 2>/dev/null
cd /opt/bookworm
mkdir app 2>/dev/null
mkdir conf 2>/dev/null
mkdir var 2>/dev/null
mkdir var/templates 2>/dev/null
mkdir var/repository 2>/dev/null
mkdir var/repository/Bestellung 2>/dev/null
mkdir var/repository/HtmlEmailTemplate 2>/dev/null
mkdir var/repository/Merkliste 2>/dev/null
mkdir var/repository/Warenkorb 2>/dev/null
mkdir var/wbh 2>/dev/null
mkdir var/wbh/aktualisierung 2>/dev/null
mkdir var/wbh/aktualisierung/eingangskorb 2>/dev/null
mkdir var/wbh/aktualisierung/ausgangskorb 2>/dev/null
mkdir var/wbh/hoerbuchkatalog 2>/dev/null
mkdir var/wbh/nutzerdaten 2>/dev/null
mkdir var/blista 2>/dev/null
mkdir var/blista/dls 2>/dev/null

exit 0
