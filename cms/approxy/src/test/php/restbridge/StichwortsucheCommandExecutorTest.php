<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/class/restbridge/Environment.php';
require_once '../../../main/php/restbridge/restbridge_configuration.php';

use PHPUnit\Framework\TestCase;
use restbridge\CommandExecutor;
use restbridge\JoomlaCmsAdapterImpl;

class StichwortsucheCommandExecutorTest extends TestCase
{


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testStichwortsucheCommand(): void
    {
        $commandExecutor = new CommandExecutor(new JoomlaCmsAdapterImpl());
        $commandName = "Stichwortsuche";
        $parameters = "mandant:06,hoerernummer:80170,stichwort:Adams";
        $result = $commandExecutor->executeCommand($commandName, $parameters);
        error_log(print_r($result), true);
        $this->assertTrue($result->isOk());

    }//end testStichwortsucheCommand()


}//end class
