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
use restbridge\ArrayHelper;
use restbridge\RestRequestReply;

class RestRequestReplyTest extends TestCase
{


    /**
     * Test.
     *
     * @return void
     *
     * @throws \JsonException
     *
     * @since version
     */
    public function testAudiobookInfo(): void
    {
        $response = '';
        try {
            $restEndpoint = $GLOBALS['restBridge']['REST_ENDPOINTS']['HoerbuchdetailsAnzeigen'];
            $requestDto = $GLOBALS['restBridge']['REQUEST_DTOS']['HoerbuchdetailsAnzeigen'];
            $restReqResp = new RestRequestReply($restEndpoint, $requestDto);
            $response = $restReqResp->execute(
                ['mandant' => '06', 'hoerernummer' => '80170', 'titelnummer' => '32901'],
                function ($ch) use ($restEndpoint) {
                    curl_setopt(
                        $ch,
                        CURLOPT_HTTPHEADER,
                        ArrayHelper::arrayCombineKeyValue($restEndpoint['headers'])
                    );
                }
            );
        } catch (Exception $e) {
            $this->fail($e->getMessage());
        }

        $this->assertNotNull($response);
        $this->assertTrue($response->wasSuccessful());
        $json = $response->getJson();
        $this->assertNotNull($json);
        error_log(print_r($json, true), 0);

    }//end testAudiobookInfo()


}//end class
