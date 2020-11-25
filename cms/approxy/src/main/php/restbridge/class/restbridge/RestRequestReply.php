<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class RestRequestReply
{

    /**
     * Description.
     *
     * @var array
     *
     * @since 1.0
     */
    private array $restEndpoint;

    /**
     * Description.
     *
     * @var array
     *
     * @since 1.0
     */
    private array $requestDto;


    /**
     * RestRequestReply constructor.
     *
     * @param array $restEndpoint
     * @param array $requestDto
     *
     * @since 1.0
     */
    public function __construct(array $restEndpoint, array $requestDto)
    {
        $this->restEndpoint = $restEndpoint;
        $this->requestDto = $requestDto;

    }//end __construct()


    /**
     * Call a REST endpoint.
     *
     * @param array $parameters Comment.
     * @param callable|null $preHttpPostCallback Comment.
     *
     * @return HttpResponse
     *
     * @throws \Exception
     *
     * @since 1.0
     */
    public function execute(array $parameters, callable $preHttpPostCallback = null): HttpResponse
    {
        $payloadIsJson = isset($this->restEndpoint['mime_type']) === false
            || $this->restEndpoint['mime_type'] == 'application/json';
        if ($payloadIsJson) {
            $body = $this->encodeJsonBody($parameters);
        } else {
            throw new \Exception('Payload not JSON');
        }

        $url = $this->restEndpoint['url'];
        $urlParameterTemplate = new Template($this->restEndpoint['parameter_template']);
        $urlParameter = $urlParameterTemplate->renderToString([$parameters]);
        $httpClient = new HttpClient();
        $method = strtoupper($this->restEndpoint['method']);

        if ($method === 'GET') {
            return $httpClient->{"http$method"}($url.$urlParameter, $preHttpPostCallback);
        } else if ($method === 'POST' || $method === 'PUT' || $method === 'DELETE') {
            return $httpClient->{"http$method"}($url.$urlParameter, $body, $preHttpPostCallback);
        }

    }//end restRequestReply()


    /**
     * Description.
     *
     * @param array $parameters Comment.
     *
     * @return string
     *
     * @throws \Exception Comment.
     *
     * @since 1.0
     */
    private function encodeJsonBody(array $parameters): string
    {
        if (isset($this->requestDto) === true) {
            try {
                $json = json_encode($this->requestDto, JSON_THROW_ON_ERROR);
                $jsonTemplate = new Template($json);
                $body = $jsonTemplate->renderToString([$parameters]);
            } catch (\JsonException $e) {
                throw new \Exception('Encoding body as JSON failed', 0, $e);
            }
        }

        return isset($body) === true ? $body : '';

    }//end decodeJsonBody()


    /**
     * TODO Implementation broken.
     *
     * @param string $bodyTemplate Comment.
     * @param array $parameters Comment.
     *
     * @return string
     *
     * @since 1.0
     */
    public function urlEncodedBody(string $bodyTemplate, array $parameters): string
    {
        $body = '';
        foreach ($bodyTemplate as $bodyKey => $bodyValue) {
            $template = new Template($bodyKey . '=' . $bodyValue);
            $bodyElt = $template->renderToString([$parameters]);
            if (empty($body) === false) {
                $body .= '&';
            }

            $body .= $bodyElt;
        }

        return $body;

    }//end urlEncodedBody()


}//end class
