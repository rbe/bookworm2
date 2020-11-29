<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

require_once __DIR__ . '/autoload.php';
require_once __DIR__ . '/RestBridgePlugin.php';

//$HOERBUCHKATALOG_URL = 'http://host.docker.internal:8080';
$HOERBUCHKATALOG_URL = 'http://hoerbuchkatalog:8080';
$HOERBUCHKATALOG_HEADERS = [
    'Accept' => 'application/json',
    'Origin' => 'audiobook.wbh-online.de',
    'X-Bookworm-Mandant' => '{mandant}',
    'X-Bookworm-Hoerernummer' => '{hoerernummer}',
];

global $mandant;
$mandant = '06';

global $restBridge;
$restBridge = [
    'DEBUG' => true,
    'TEMPLATE_NAME_PREFIX' => 'Bookworm',
    'REST_ENDPOINTS' => [
        // Suche
        'Stichwortsuche' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/katalog/stichwort/{stichwort}',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Hörbuchdetails
        'HoerbuchdetailsAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/katalog/{titelnummer}/details',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Merkliste
        'MerklisteHinzufuegen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'POST',
            'parameter_template' => '/hoerbuchkatalog/v1/merkliste/{titelnummer}',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'MerklisteLoeschen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'DELETE',
            'parameter_template' => '/hoerbuchkatalog/v1/merkliste/{titelnummer}',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'MerklisteAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/merkliste',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Warenkorb
        'WarenkorbHinzufuegen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'POST',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb/{titelnummer}',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbLoeschen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'DELETE',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb/{titelnummer}',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbBestellerAnzeigen' => [ // Hörerdaten im Bestellformular
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererdaten',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbBestellen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'POST',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb/bestellen',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Hörerdaten
        'HoererdatenAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererdaten',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Hörerarchiv
        'BelastungenAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererarchiv/belastungen',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'BestellkarteAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererarchiv/bestellkarten',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'ErledigteBestellkartenAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererarchiv/erledigteBestellkarten',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
    ],
    'REQUEST_DTOS' => [
        // Suche
        'Stichwortsuche' => [
        ],
        // Hörbuchdetails
        'HoerbuchdetailsAnzeigen' => [
        ],
        // Merkliste
        'MerklisteHinzufuegen' => [ // wird per JavaScript erledigt
        ],
        'MerklisteLoeschen' => [ // wird per JavaScript erledigt
        ],
        'MerklisteAnzeigen' => [
        ],
        // Warenkorb
        'WarenkorbHinzufuegen' => [ // wird per JavaScript erledigt
        ],
        'WarenkorbLoeschen' => [ // wird per JavaScript erledigt
        ],
        'WarenkorbAnzeigen' => [
        ],
        'WarenkorbBestellen' => [ // wird per JavaScript erledigt
            'hoerername' => '{hoerername}',
            'hoereremail' => '{hoereremail}',
            'bemerkung' => '{bemerkung}',
            'bestellkarteMischen' => '{bestellkarteMischen}',
            'alteBestellkarteLoeschen' => '{alteBestellkarteLoeschen}',
        ],
        // Hörerdaten
        'HoererdatenAnzeigen' => [
        ],
        // Hörerarchiv
        'BelastungenAnzeigen' => [
        ],
        'BestellkarteAnzeigen' => [
        ],
        'ErledigteBestellkartenAnzeigen' => [
        ],
    ],
    'PLUGIN' => new RestBridgePlugin(),
];
