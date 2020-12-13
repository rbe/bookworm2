<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

use restbridge\Debugging;
use restbridge\Environment;

require_once __DIR__ . '/restbridge_configuration.php';

// Check environment.
$environment = new Environment();
$environment->checkPhpVersion();

function isRestBridgeDebugLog()
{
    global $restBridge;
    return $restBridge['DEBUG'];
}//end isRestBridgeDebugLog()

function restBridgeInfoLog($msg)
{
    error_log('INFO: ' . $msg, 0);
}//end restBridgeInfoLog()

function restBridgeWarningLog($msg)
{
    error_log('WARNING: ' . $msg, 0);
}//end restBridgeWarningLog()

function restBridgeErrorLog($msg)
{
    error_log('ERROR: ' . $msg, 0);
}//end restBridgeErrorLog()

function restBridgeDebugLog($msg)
{
    if (isRestBridgeDebugLog()) {
        error_log('DEBUG: ' . $msg, 0);
    }
}//end restBridgeDebugLog()

// Enable debugging?
if (isRestBridgeDebugLog() === true) {
    Debugging::enable();
}
