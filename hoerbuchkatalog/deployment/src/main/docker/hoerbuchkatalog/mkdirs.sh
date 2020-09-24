#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

pushd /var/local

set +o errexit
mkdir conf 2>/dev/null
mkdir java_debug 2>/dev/null
mkdir templates 2>/dev/null
mkdir repository 2>/dev/null
mkdir repository/Bestellung 2>/dev/null
mkdir repository/HtmlEmailTemplate 2>/dev/null
mkdir repository/Merkliste 2>/dev/null
mkdir repository/Warenkorb 2>/dev/null
mkdir wbh 2>/dev/null
mkdir wbh/aktualisierung 2>/dev/null
mkdir wbh/aktualisierung/eingangskorb 2>/dev/null
mkdir wbh/aktualisierung/ausgangskorb 2>/dev/null
mkdir wbh/hoerbuchkatalog 2>/dev/null
mkdir wbh/nutzerdaten 2>/dev/null
mkdir blista 2>/dev/null
mkdir blista/dls 2>/dev/null
set -o errexit

popd

exit 0
