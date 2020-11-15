<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class Environment
{

    private const MIN_PHP_VERSION = 70400;


    function checkCalender(): void
    {
        date_default_timezone_set('UTC');
    }//end checkCalender()


    function checkPhpVersion(): void
    {
        if (!defined('PHP_VERSION_ID')) {
            $version = explode('.', PHP_VERSION);
            define('PHP_VERSION_ID', ($version[0] * 10000 + $version[1] * 100 + $version[2]));
        }
        if (PHP_VERSION_ID < self::MIN_PHP_VERSION) {
            die('Unsupported PHP version ' . PHP_VERSION_ID);
        }
    }//end checkPhpVersion()


}//end class
