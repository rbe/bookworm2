<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

namespace restbridge;

final class HttpClient
{

    /**
     * @param string $url
     * @param callable|null $preCallback
     *
     * @return object
     *
     * @since version
     */
    function httpGET(string $url, callable $preCallback = null): object
    {
        $ch = $this->initCurl($url);
        if ($preCallback) {
            $preCallback($ch);
        }
        $responseBody = curl_exec($ch);
        $info = $this->responseInfo($ch);
        curl_close($ch);
        return new HttpResponse($info['statuscode'], $responseBody);
    }//end httpGet()

    /**
     * @param string $url
     *
     * @return false|resource
     *
     * @since version
     */
    private function initCurl(string $url)
    {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        $this->stdCurlOpt($ch);
        return $ch;
    }//end httpPost()

    /**
     * @param $ch
     *
     * @since version
     */
    private function stdCurlOpt($ch): void
    {
        curl_setopt($ch, CURLOPT_USERAGENT, 'restbridge/1.0 (+https://www.art-of-coding.eu/restbridge.html)');
        curl_setopt($ch, CURLOPT_HTTP_VERSION, '1.1');
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
        curl_setopt($ch, CURLOPT_MAXREDIRS, 1);
    }//end responseInfo()

    /**
     * @param $ch
     *
     * @return array
     *
     * @since version
     */
    private function responseInfo($ch): array
    {
        $errno = curl_errno($ch);
        $info = curl_getinfo($ch);
        $myinfo = [
            'statuscode' => $info['http_code'],
            'error' => implode(' ', [$errno, trim(curl_error($ch)), trim(curl_strerror($errno))]),
            'url' => $info['url'],
            'content-type' => $info['content_type']
        ];
        // nginx HTTP 444
        $gotEmptyReply = $errno === 52
            && strpos($myinfo['error'], 'Empty reply') !== false;
        if ($gotEmptyReply) {
            $myinfo['statuscode'] = 444;
        }
        return $myinfo;
    }//end setHeader()

    /**
     * @param string $url
     * @param string $requestBody
     * @param callable|null $preCallback
     *
     * @return object
     *
     * @since version
     */
    function httpPOST(string $url, string $requestBody, callable $preCallback = null): object
    {
        $ch = $this->initCurl($url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBody);
        if ($preCallback) {
            $preCallback($ch);
        }
        error_log(sprintf("POST %s", $url), 0);
        $responseBody = curl_exec($ch);
        $info = $this->responseInfo($ch);
        curl_close($ch);
        return new HttpResponse($info['statuscode'], $responseBody);
    }//end initCurl()

    /**
     * @param $ch
     * @param array $headers
     *
     * @since version
     */
    private function setHeader($ch, array $headers): void
    {
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    }//end stdCurlOpt()

}//end class
