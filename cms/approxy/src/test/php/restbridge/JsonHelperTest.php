<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

use PHPUnit\Framework\TestCase;
use restbridge\JsonHelper;

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/class/restbridge/JsonHelper.php';

class JsonHelperTest extends TestCase
{


    /**
     * Test a JSON map: one row with values.
     *
     * @return void
     *
     * @throws JsonException
     *
     * @since version
     */
    public function testJsonMap(): void
    {
        $json = '{"a":1,"b":2}';
        $this->assertThat($json, self::isJson());
        $array = json_decode($json, true, 512, JSON_THROW_ON_ERROR);
        $this->assertIsArray($array);
        print_r($array);
        $this->assertCount(2, $array);
        $jsonHelper = new JsonHelper($array);
        $this->assertTrue($jsonHelper->isSingleRow());
        $this->assertEquals(1, $jsonHelper->numberOfRows());

    }//end testJsonMap()


    /**
     * Test a JSON array containing maps: rows with values.
     *
     * @return void
     *
     * @throws JsonException
     *
     * @since version
     */
    public function testJsonArrayWith1Map(): void
    {
        $json = '[{"row1-a":1}]';
        $this->assertThat($json, self::isJson());
        $array = json_decode($json, true, 512, JSON_THROW_ON_ERROR);
        $this->assertIsArray($array);
        print_r($array);
        $this->assertCount(1, $array);
        $jsonHelper = new JsonHelper($array);
        $this->assertEquals(1, $jsonHelper->numberOfRows());

    }//end testJsonArrayWith1Map()


    /**
     * Test a JSON array containing maps: rows with values.
     *
     * @return void
     *
     * @throws JsonException
     *
     * @since version
     */
    public function testJsonArrayWith2Maps(): void
    {
        $json = '[{"row1-a":1,"row1-b":2}, {"row2-a":3,"row2-b":4}]';
        $this->assertThat($json, self::isJson());
        $array = json_decode($json, true, 512, JSON_THROW_ON_ERROR);
        $this->assertIsArray($array);
        $this->assertCount(2, $array);
        $jsonHelper = new JsonHelper($array);
        $this->assertEquals(2, $jsonHelper->numberOfRows());

    }//end testJsonArrayWith2Maps()


}//end class
