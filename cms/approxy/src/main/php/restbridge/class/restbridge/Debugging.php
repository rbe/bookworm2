<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class Debugging
{


    /**
     * Comment.
     *
     * @since 1.0
     */
    static public function enable()
    {
        error_reporting(-1);
        ini_set('display_startup_errors', '1');
        ini_set('display_errors', '1');
        ini_set('error_log', '/proc/self/fd/2');
        ini_set('error_reporting', 'E_ALL');
        ini_set('log_errors', '1');
        ini_set('log_errors_max_length', '0');
        ini_set('report_memleaks', '1');
    }//end enable()


}//end class
