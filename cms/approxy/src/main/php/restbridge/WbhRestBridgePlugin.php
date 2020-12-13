<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

require_once __DIR__ . '/autoload.php';

use restbridge\AbstractRestBridgePlugin;
use restbridge\CommandExecutor;
use restbridge\JoomlaCmsAdapterImpl;

final class WbhRestBridgePlugin extends AbstractRestBridgePlugin
{

    private CommandExecutor $commandExecutor;


    /**
     * WbhRestBridgePlugin constructor.
     *
     * @since 1.0
     */
    public function __construct()
    {
        parent::__construct(new JoomlaCmsAdapterImpl());
        $this->commandExecutor = new CommandExecutor($this->cmsAdapter);

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
        $parameters .= 'mandant:' . $mandant . ',hoerernummer:' . $hoerernummer;
        $bookwormCookie = $this->cmsAdapter->getCookie('bookworm');
        if (isset($bookwormCookie) === true) {
            $strings = explode('--', $bookwormCookie);
            $bestellungSessionId = $strings[1];
            $parameters .= ',bestellungSessionId:' . $bestellungSessionId;
        }

        restBridgeDebugLog('#customizeParameters: $parameters=' . print_r($parameters, true));
    }//end customizeParameters()


    /**
     * Description.
     *
     * @return string Comment.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    public function afterContentPrepared(): string
    {
        $hoerernummer = $this->cmsAdapter->getUserValue('cb_hoerernummer');
        if (isset($hoerernummer) === false || $hoerernummer === '') {
            $hoerernummer = '00000';
        }

        $this->bookwormCookie($hoerernummer);
        return "<script type='module'>\n"
            . "import {Wbhonline} from '/hoerbuchkatalog/js/wbhonline.js';\n"
            . "document.addEventListener('DOMContentLoaded', (event) => {\n"
            . "  const wbhonline = new Wbhonline();\n"
            . "  wbhonline.initialize();\n"
            . "});\n"
            . "</script>\n";

    }//end afterContentPrepared()


    /**
     * Description.
     *
     * @param string $hoerenummer Comment.
     *
     * @return void Comment.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    private function bookwormCookie(string $hoerenummer): void
    {
        $bookworm = $this->cmsAdapter->getCookie('bookworm');
        if ($bookworm === '') {
            global $mandant;
            $commandResult = $this->commandExecutor->executeCommand('BestellungSessionId',
                'mandant:' . $mandant . ',hoerernummer:' . $hoerenummer);
            $data = $commandResult->getData();
            restBridgeDebugLog('Antwort ' . print_r($data, true) . ' erhalten');
            $bestellungSessionId = $data['bestellungSessionId'];
            restBridgeDebugLog('BestellungSessionId ' . $bestellungSessionId . ' erhalten');
            $value = $hoerenummer . '--' . $bestellungSessionId ?? '';
            $this->cmsAdapter->setCookieOnce('bookworm', $value);
        }

    }//end bookwormCookie()


}//end class
