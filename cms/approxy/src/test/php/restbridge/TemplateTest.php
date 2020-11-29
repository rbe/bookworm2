<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

use PHPUnit\Framework\TestCase;
use restbridge\Template;

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/class/restbridge/Template.php';

class TemplateTest extends TestCase
{


    /**
     * Test one row with data.
     *
     * @return void
     *
     * @since version
     */
    public function test1Row(): void
    {
        echo "test1Row\n";
        $template = new Template("This template shows: {key}\n{bool}\n");
        $rowsWithValues = [['key' => 'value', 'bool' => true]];
        print_r($rowsWithValues);
        $result = $template->renderToString($rowsWithValues);
        $this->assertNotNull($result);
        print_r($result);

    }//end test1Row()


    /**
     * Test one row with data.
     *
     * @return void
     *
     * @since version
     */
    public function test2Rows(): void
    {
        echo "test2Rows\n";
        $template = new Template("This template shows: {key}\n");
        $rowsWithValues = [
            ['key' => 'value1'],
            ['key' => 'value2']
        ];
        print_r($rowsWithValues);
        $result = $template->renderToString($rowsWithValues);
        $this->assertNotNull($result);
        print_r($result);

    }//end test2Rows()


}//end class
