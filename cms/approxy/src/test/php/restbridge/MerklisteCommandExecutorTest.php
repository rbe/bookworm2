<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/class/restbridge/Environment.php';
require_once '../../../main/php/restbridge/restbridge_configuration.php';

use PHPUnit\Framework\TestCase;
use restbridge\CommandExecutor;
use restbridge\JoomlaCmsAdapterImpl;

class MerklisteCommandExecutorTest extends TestCase
{


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testMerklisteHinzufuegen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "MerklisteHinzufuegen";
        $parameters = "mandant:06,hoerernummer:80007,titelnummer:21052";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertEquals(['result' => true], $result);

    }//end testMerklisteHinzufuegen()


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testMerklisteLoeschen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "MerklisteLoeschen";
        $parameters = "mandant:06,hoerernummer:80007,titelnummer:21052";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertEquals(['result' => true], $result);

    }//end testMerklisteLoeschen()


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testMerklisteAnzeigen(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "MerklisteAnzeigen";
        $parameters = "mandant:06,hoerernummer:80007,bestellungSessionId:";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result, true), 0);
        $this->assertTrue($result->isOk());
        $this->assertGreaterThan(0, count($result->getData()));

    }//end testMerklisteAnzeigen()


}//end class
