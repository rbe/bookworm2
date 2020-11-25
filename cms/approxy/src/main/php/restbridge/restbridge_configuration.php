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
    'Content-Type' => 'application/json',
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
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Hörbuchdetails
        'HoerbuchdetailsAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/katalog/details/{titelnummer}',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Merkliste
        'MerklisteHinzufuegen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'POST',
            'parameter_template' => '/hoerbuchkatalog/v1/merkliste',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'MerklisteLoeschen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'DELETE',
            'parameter_template' => '/hoerbuchkatalog/v1/merkliste',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'MerklisteAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/merkliste',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Warenkorb
        'WarenkorbHinzufuegen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'POST',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbLoeschen' => [ // wird per JavaScript erledigt
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'DELETE',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/warenkorb',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'WarenkorbBestellerAnzeigen' => [ // Hörerdaten im Bestellformular
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererdaten',
            'mime_type' => 'application/json',
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
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        // Hörerarchiv
        'BelastungenAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererarchiv/belastungen',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'BestellkarteAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererarchiv/bestellkarten',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
        'ErledigteBestellkartenAnzeigen' => [
            'url' => $HOERBUCHKATALOG_URL,
            'method' => 'GET',
            'parameter_template' => '/hoerbuchkatalog/v1/hoererarchiv/erledigteBestellkarten',
            'mime_type' => 'application/json',
            'headers' => $HOERBUCHKATALOG_HEADERS,
        ],
    ],
    'REQUEST_DTOS' => [
        // Suche
        'Stichwortsuche' => [
            'stichwort' => '{stichwort}'
        ],
        // Hörbuchdetails
        'HoerbuchdetailsAnzeigen' => [
            // TODO 'aghNummer' => '{aghNummer}',
            'titelnummer' => '{titelnummer}',
        ],
        // Merkliste
        'MerklisteHinzufuegen' => [ // wird per JavaScript erledigt
            'titelnummer' => '{titelnummer}'
        ],
        'MerklisteLoeschen' => [ // wird per JavaScript erledigt
            'titelnummer' => '{titelnummer}'
        ],
        'MerklisteAnzeigen' => [
        ],
        // Warenkorb
        'WarenkorbHinzufuegen' => [ // wird per JavaScript erledigt
            'titelnummer' => '{titelnummer}'
        ],
        'WarenkorbLoeschen' => [ // wird per JavaScript erledigt
            'titelnummer' => '{titelnummer}'
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

//
// DO NOT MODIFY CODE BELOW THIS COMMENT.
//

// Check environment.
use restbridge\Debugging;
use restbridge\Environment;

$environment = new Environment();
$environment->checkPhpVersion();
// Enable debugging?
if ($restBridge['DEBUG'] === true) {
    Debugging::enable();
    //error_log('restbridge_configuration: ' . print_r($restBridge, true), 0);
}

function rbdebug($msg)
{
    if ($GLOBALS['$restBridge']['DEBUG'] === true) {
        error_log($msg, 0);
    }
}//end rbdebug()
