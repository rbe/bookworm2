<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/restbridge_configuration.php';

use PHPUnit\Framework\TestCase;
use restbridge\CommandExecutor;
use restbridge\JoomlaCmsAdapterImpl;

class WarenkorbCommandExecutorTest extends TestCase
{


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testWarenkorbHinzufuegen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "WarenkorbHinzufuegen";
        $parameters = "mandant:06,hoerernummer:80007,titelnummer:21052";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertEquals(['result' => true], $result);

    }//end testWarenkorbHinzufuegen()


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testWarenkorbLoeschen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "WarenkorbLoeschen";
        $parameters = "mandant:06,hoerernummer:80007,titelnummer:21052";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertEquals(['result' => true], $result);

    }//end testWarenkorbLoeschen()


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testWarenkorbAnzeigen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "WarenkorbAnzeigen";
        $parameters = "mandant:06,hoerernummer:80007";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertGreaterThan(0, count($result));

    }//end testWarenkorbAnzeigen()


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testWarenkorbBestellen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "WarenkorbBestellen";
        $parameters = "mandant:06,hoerernummer:80007,hoerername:Herbert Hoerer,hoereremail:herbert@example.com,bemerkung:Eine kleine Bemerkung,bestellkarteMischen:false,alteBestellkarteLoeschen:false";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertGreaterThan(0, count($result));

    }//end testWarenkorbBestellen()


}//end class
