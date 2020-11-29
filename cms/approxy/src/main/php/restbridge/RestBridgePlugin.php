<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

require_once __DIR__ . '/autoload.php';

use restbridge\AbstractRestBridgePlugin;
use restbridge\JoomlaCmsAdapterImpl;

final class RestBridgePlugin extends AbstractRestBridgePlugin
{


    /**
     * RestBridgePlugin constructor.
     *
     * @since 1.0
     */
    public function __construct()
    {
        $cmsAdapter = new JoomlaCmsAdapterImpl();
        parent::__construct($cmsAdapter);

    }//end __construct()


    /**
     * Description.
     *
     * @param string $commandName Comment.
     * @param string $parameters Comment.
     *
     * @return void
     *
     * @since 1.0
     */
    public function customizeParameters(string $commandName, string &$parameters)
    {
        $hoerernummer = $this->cmsAdapter->getUserValue('cb_hoerernummer');
        if (isset($hoerernummer) === false || $hoerernummer === '') {
            $hoerernummer = '00000';
        }

        if (strlen($parameters) > 0) {
            $parameters .= ',';
        }

        global $mandant;
        $parameters .= 'mandant:'.$mandant.',hoerernummer:'.$hoerernummer;
        restBridgeDebugLog('#customizeParameters: $parameters='.print_r($parameters, true));

    }//end modifyParameters()


}//end class
