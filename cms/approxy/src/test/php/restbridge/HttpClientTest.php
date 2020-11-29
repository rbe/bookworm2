<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

use PHPUnit\Framework\TestCase;
use restbridge\HttpClient;

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/restbridge.php';

class HttpClientTest extends TestCase
{


    /**
     * Test simple HTTP GET.
     *
     * @return void
     *
     * @since version
     */
    public function testHttpsGET(): void
    {
        $httpClient = new HttpClient();
        $result = $httpClient->httpGET('https://www.google.de');
        $this->assertStringStartsWith(
            '<!doctype html><html itemscope="" itemtype="http://schema.org/WebPage" lang="de">',
            $result->getBody()
        );

    }//end testHttpsGetPage()


}//end class
