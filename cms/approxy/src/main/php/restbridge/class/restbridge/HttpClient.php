<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class HttpClient
{


    /**
     * Description.
     *
     * @param string $url Comment.
     * @param callable|null $preCallback Comment.
     *
     * @return HttpResponse
     *
     * @throws \Exception
     *
     * @since 1.0
     */
    public function httpGET(string $url, callable $preCallback = null): HttpResponse
    {
        $ch = $this->initCurl($url);
        if (isset($preCallback) === true) {
            $preCallback($ch);
        }

        curl_setopt($ch, CURLOPT_VERBOSE, true);
        $responseBody = curl_exec($ch);
        if ($responseBody === false) {
            throw new \Exception('Empty response body');
        }

        $info = $this->responseInfo($ch);
        curl_close($ch);
        return new HttpResponse($info['statuscode'], $responseBody);

    }//end httpGET()


    /**
     * Description.
     *
     * @param string $url Comment.
     * @param string|null $requestBody Comment.
     * @param callable|null $preCallback Comment.
     *
     * @return HttpResponse
     *
     * @throws \Exception
     *
     * @since 1.0
     */
    public function httpPOST(string $url, string $requestBody = null, callable $preCallback = null): HttpResponse
    {
        $ch = $this->initCurl($url);
        curl_setopt($ch, CURLOPT_POST, true);
        if (isset($requestBody) === true) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBody);
        }
        if (isset($preCallback) === true) {
            $preCallback($ch);
        }

        restBridgeDebugLog(sprintf("POST %s\n%s", $url, $requestBody));
        curl_setopt($ch, CURLOPT_VERBOSE, true);
        $responseBody = curl_exec($ch);
        if ($responseBody === false) {
            throw new \Exception('Empty response body');
        }

        $info = $this->responseInfo($ch);
        curl_close($ch);
        return new HttpResponse($info['statuscode'], $responseBody);

    }//end httpPOST()


    /**
     * Description.
     *
     * @param string $url Comment.
     * @param string|null $requestBody Comment.
     * @param callable|null $preCallback Comment.
     *
     * @return HttpResponse
     *
     * @throws \Exception
     *
     * @since 1.0
     */
    public function httpPUT(string $url, string $requestBody = null, callable $preCallback = null): HttpResponse
    {
        $ch = $this->initCurl($url);
        curl_setopt($ch, CURLOPT_PUT, true);
        if (isset($requestBody) === true) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBody);
        }
        if (isset($preCallback) === true) {
            $preCallback($ch);
        }

        restBridgeDebugLog(sprintf("PUT %s\n%s", $url, $requestBody));
        curl_setopt($ch, CURLOPT_VERBOSE, true);
        $responseBody = curl_exec($ch);
        if ($responseBody === false) {
            throw new \Exception('Empty response body');
        }

        $info = $this->responseInfo($ch);
        curl_close($ch);
        return new HttpResponse($info['statuscode'], $responseBody);

    }//end httpPUT()


    /**
     * Description.
     *
     * @param string $url Comment.
     * @param string|null $requestBody Comment.
     * @param callable|null $preCallback Comment.
     *
     * @return HttpResponse
     *
     * @throws \Exception
     *
     * @since 1.0
     */
    public function httpDELETE(string $url, string $requestBody = null, callable $preCallback = null): HttpResponse
    {
        $ch = $this->initCurl($url);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
        if (isset($requestBody) === true) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBody);
        }
        if (isset($preCallback) === true) {
            $preCallback($ch);
        }

        restBridgeDebugLog(sprintf("DELETE %s\n%s", $url, $requestBody));
        curl_setopt($ch, CURLOPT_VERBOSE, true);
        $responseBody = curl_exec($ch);
        if ($responseBody === false) {
            throw new \Exception('Empty response body');
        }

        $info = $this->responseInfo($ch);
        curl_close($ch);
        return new HttpResponse($info['statuscode'], $responseBody);

    }//end httpDELETE()


    /**
     * Description.
     *
     * @param string $url Comment.
     *
     * @return false|resource
     *
     * @since 1.0
     */
    private function initCurl(string $url)
    {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_USERAGENT, 'restbridge/1.0 (+https://www.art-of-coding.eu/restbridge.html)');
        curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
        curl_setopt($ch, CURLOPT_MAXREDIRS, 1);
        return $ch;

    }//end httpPost()


    /**
     * Description.
     *
     * @param $ch Comment.
     *
     * @return array
     *
     * @since 1.0
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
        $gotEmptyReply = $errno === 52 && strpos($myinfo['error'], 'Empty reply') !== false;
        if ($gotEmptyReply) {
            $myinfo['statuscode'] = 444;
        }

        return $myinfo;

    }//end setHeader()


    /**
     * Description.
     *
     * @param       $ch      Comment.
     * @param array $headers Comment.
     *
     * @since 1.0
     */
    private function setHeader($ch, array $headers): void
    {
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    }//end stdCurlOpt()


}//end class
