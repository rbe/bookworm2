<?php
declare(strict_types=1);

use PHPUnit\Framework\TestCase;

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/HttpClient.php';

/**
 * Test HttpClient.
 */
class HttpClientTest extends TestCase
{

    /**
     * Test simple HTTP GET.
     *
     * @return void
     */
    final public function testHttpsGetPage(): void
    {
        $httpClient = new HttpClient();
        $result = $httpClient->httpGet('https://www.google.de');
        $this->assertStringStartsWith(
            '<!doctype html><html itemscope="" itemtype="http://schema.org/WebPage" lang="de">',
            $result
        );
    }//end testHttpsGetPage()

    /**
     * Test simple HTTP POST.
     *
     * @return void
     */
    final public function testHttpsPostPage(): void
    {
        $httpClient = new HttpClient();
        $result = $httpClient->httpPost('https://www.google.de', array());
        $this->assertStringStartsWith(
            '<!doctype html><html itemscope="" itemtype="http://schema.org/WebPage" lang="de">',
            $result
        );
    }//end testHttpsGetPage()

}//end class
