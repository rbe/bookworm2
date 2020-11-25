<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/restbridge_configuration.php';

use PHPUnit\Framework\TestCase;
use restbridge\ArrayHelper;
use restbridge\RestRequestReplyCommandImpl;
use restbridge\Template;

class RestRequestReplyCommandImplTest extends TestCase
{


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testSuche(): void
    {
        $restEndpoint = $GLOBALS['restBridge']['REST_ENDPOINTS']['Stichwortuche'];
        $requestDto = $GLOBALS['restBridge']['REQUEST_DTOS']['Stichwortsuche'];
        $headers = $restEndpoint['headers'];
        foreach ($headers as $k => $v) {
            $template = new Template($v);
            $headers[$k] = $template->renderToString([['mandant' => '06', 'hoerernummer' => '80170']]);
        }
        $command = new RestRequestReplyCommandImpl(
            [
                'endpoint' => $restEndpoint,
                'requestDto' => $requestDto,
                'urlParameters' => ['mandant' => '06', 'hoerernummer' => '80170', 'stichwort' => 'Adams'],
                'preHttpPostCallback' => function ($ch) use ($headers) {
                    curl_setopt(
                        $ch,
                        CURLOPT_HTTPHEADER,
                        ArrayHelper::arrayCombineKeyValue($headers)
                    );
                },
            ]
        );
        $json = $command->execute();
        $this->assertNotNull($json);
        error_log(print_r($json, true), 0);

    }//end testSuche()


    /**
     * Test REST request and reply.
     *
     * @return void
     *
     * @since version
     */
    public function testAudiobookInfo(): void
    {
        $restEndpoint = $GLOBALS['restBridge']['REST_ENDPOINTS']['AudiobookInfo'];
        $requestDto = $GLOBALS['restBridge']['REQUEST_DTOS']['AudiobookInfo'];
        $command = new RestRequestReplyCommandImpl(
            [
                'endpoint' => $restEndpoint,
                'requestDto' => $requestDto,
                'urlParameters' => ['mandant' => '06', 'hoerernummer' => '80170', 'titelnummer' => '32901'],
                'preHttpPostCallback' => function ($ch) use ($restEndpoint) {
                    curl_setopt(
                        $ch,
                        CURLOPT_HTTPHEADER,
                        ArrayHelper::arrayCombineKeyValue($restEndpoint['headers'])
                    );
                },
            ]
        );
        $json = $command->execute();
        $this->assertNotNull($json);
        error_log(print_r($json, true), 0);

    }//end testAudiobookInfo()


}//end class
