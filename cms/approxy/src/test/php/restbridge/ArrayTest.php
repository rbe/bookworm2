<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

use PHPUnit\Framework\TestCase;

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';

class ArrayTest extends TestCase
{


    /**
     * Test merge of single row array with an array.
     *
     * @return void
     *
     * @since version
     */
    public function testSingleRowArrayMerge(): void
    {
        $arr = ['param' => 'value'];
        $result = ['a' => '1', 'b' => '2'];
        print_r(array_merge($arr, $result));
        $this->assertTrue(true);

    }//end testSingleRowArrayMerge()


    /**
     * Test merge of multi-row array with an array.
     *
     * @return void
     *
     * @since version
     */
    public function testMultiRowArrayMerge(): void
    {
        $arr = ['param' => 'value'];
        $result = [['a' => '1', 'b' => '2'], ['c' => '3', 'd' => '4']];
        foreach ($result as $key => $value) {
            $result[$key] = array_merge($arr, $value);
            print_r($result[$key]);
        }
        $this->assertTrue(true);

    }//end testSingleRowArrayMerge()


}//end class
