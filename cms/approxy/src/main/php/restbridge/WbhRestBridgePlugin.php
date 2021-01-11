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
     * @param string $urlParameters Comment.
     * @return string Comment.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    public function beforeCommandExecution(string $commandName, string &$urlParameters): string
    {
        $hoerernummer = $this->cmsAdapter->getUserValue('cb_hoerernummer');
        if (isset($hoerernummer) === false || $hoerernummer === '') {
            $hoerernummer = '00000';
        }

        $this->bookwormCookie($hoerernummer);
        return '';

    }//end beforeContentPrepared()


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
            list($_ignore, $bestellungSessionId) = $this->explodeCookie($bookwormCookie);
            $parameters .= ',bestellungSessionId:' . $bestellungSessionId;
        }

        restBridgeDebugLog('customizeParameters: $parameters=' . print_r($parameters, true));
    }//end customizeParameters()


    /**
     * Description.
     *
     * @param string $commandName Comment.
     * @param string $urlParameters Comment.
     * @param string $content Comment.
     *
     * @return string Comment.
     *
     * @since 1.0
     */
    public function afterCommandExecution(string $commandName, string $urlParameters, string &$content): string
    {
        return '';
    }//end afterContentPrepared()


    /**
     * Description.
     *
     * @param string $hoerernummer Comment.
     *
     * @return void Comment.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    private function bookwormCookie(string $hoerernummer): void
    {
        $bookwormCookie = $this->cmsAdapter->getCookie('bookworm');
        if ($bookwormCookie === '') {
            $bestellungSessionId = $this->bestellungSessionId($hoerernummer);
        } else {
            list($_ignore, $bestellungSessionId) = $this->explodeCookie($bookwormCookie);
        }

        if (isset($bestellungSessionId) === true) {
            $value = $hoerernummer . '--' . $bestellungSessionId;
            // TODO cookie is set more than once
            $this->cmsAdapter->setCookie('bookworm', $value);
        } else {
            restBridgeErrorLog('bookwormCookie: Could not create cookie');
        }
    }//end bookwormCookie()


    /**
     * Description.
     *
     * @param string $bookwormCookie Comment.
     *
     * @return array Comment.
     *
     * @since 1.0
     */
    private function explodeCookie(string $bookwormCookie): array
    {
        $strings = explode('--', $bookwormCookie);
        $hoerernummer = $strings[0];
        $bestellungSessionId = $strings[1];
        return [$hoerernummer, $bestellungSessionId];
    }//end explodeCookie()


    /**
     * Description.
     *
     * @param string $hoerernummer Comment.
     *
     * @return string Comment.
     *
     * @throws Exception Comment.
     *
     * @since 1.0
     */
    private function bestellungSessionId(string $hoerernummer): string
    {
        global $mandant;
        $commandResult = $this->commandExecutor->executeCommand('BestellungSessionId',
            'mandant:' . $mandant . ',hoerernummer:' . $hoerernummer);
        $data = $commandResult->getData();
        $bestellungSessionId = $data['bestellungSessionId'];
        restBridgeDebugLog('bestellungSessionId: ' . print_r($data, true)
            . ', BestellungSessionId ' . $bestellungSessionId . ' erhalten');
        return $bestellungSessionId;
    }//end bestellungSessionId()


}//end class
