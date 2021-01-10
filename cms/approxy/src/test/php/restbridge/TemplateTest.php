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
require_once '../../../main/php/restbridge/restbridgeCommons.php';

class TemplateTest extends TestCase
{


    /**
     * Test with no data.
     *
     * @return void
     *
     * @since version
     */
    public function test0Row(): void
    {
        echo "test0Row\n";
        $template = new Template("This template shows {meta.count} rows: {key} and {bool}\n");
        $rowsWithValues = [];
        $result = $template->renderToString(['count' => 0], $rowsWithValues, [['bool'=>'nixda']]);
        $this->assertNotNull($result);
        print_r($result);

    }//end test0Row()


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
        $template = new Template("This template shows {meta.count} rows: {key} and {bool}, empty '{empty}'\n");
        $rowsWithValues = [
            ['key' => 'key_value', 'bool' => true, 'empty' => ''],
        ];
        $result = $template->renderToString(['count' => 1], $rowsWithValues);
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
        $template = new Template("This template shows {meta.count} rows: {key}\n");
        $rowsWithValues = [
            ['key' => 'value1'],
            ['key' => 'value2'],
        ];
        $result = $template->renderToString(['count' => 2], $rowsWithValues);
        $this->assertNotNull($result);
        print_r($result);

    }//end test2Rows()


}//end class
