<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/autoload.php';

use PHPUnit\Framework\TestCase;
use restbridge\Debugging;
use restbridge\Environment;
use restbridge\RestReqResp;

Environment::ensure();
Debugging::enable();

require_once '../../../main/php/restbridge/restbridge_configuration.php';

class RestRequestReplyTest extends TestCase
{

    final public function testAudiobookInfo(): void
    {
        // TODO Parameter aus Joomla füllen
        $response = RestReqResp::restRequestReply(REST_ENDPOINTS['AudiobookInfo'],
            ['mandant' => '06', 'hoerernummer' => '80170', 'titelnummer' => '32901'],
            function ($ch) {
                curl_setopt($ch, CURLOPT_HTTPHEADER, [
                    'Content-Type: application/json',
                    'Origin: audiobook.wbh-online.de'
                ]);
            });
        $this->assertNotNull($response);
        $this->assertTrue($response->wasSuccessful());
        $json = $response->getJson();
        $this->assertNotNull($json);
        error_log(print_r($json, true), 0);
    }

}
