<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

require_once __DIR__ . '/autoload.php';
require_once __DIR__ . '/RestBridgePlugin.php';

global $restBridge;
$restBridge = [
    'DEBUG' => true,
    'TEMPLATE_NAME_PREFIX' => 'Bookworm',
    'REST_ENDPOINTS' => [
        'AudiobookInfo' => [
            'url' => 'https://hoerbuchdienst.shard3.audiobook.wbh-online.de',
            'method' => 'POST',
            'parameter_template' => '/info/audiobook',
            'mime_type' => 'application/json',
            'headers' => [
                'Content-Type' => 'application/json',
                'Origin' => 'audiobook.wbh-online.de',
            ],
        ],
    ],
    'REQUEST_DTOS' => [
        'AudiobookInfo' => [
            'mandant' => '{mandant}',
            'hoerernummer' => '{hoerernummer}',
            'aghNummer' => '{aghNummer}',
            'titelnummer' => '{titelnummer}',
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
    error_log('restbridge_configuration: ' . print_r($restBridge, true), 0);
}

function rbdebug($msg)
{
    if ($GLOBALS['$restBridge']['DEBUG'] === true) {
        error_log($msg, 0);
    }
}//end rbdebug()
