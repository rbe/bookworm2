<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

namespace restbridge;

use JsonException;

final class RestReqResp
{

    /**
     * @param array $restEndpoint
     * @param array $parameters
     * @param callable|null $prePostCallback
     *
     * @return HttpResponse
     *
     * @throws \Exception
     * @since version
     */
    static function restRequestReply(array $restEndpoint, array $parameters, callable $prePostCallback = null): HttpResponse
    {
        $urlParameter = Template::renderTemplateToString($restEndpoint['parameter_template'], [$parameters]);
        $payloadIsJson = !$restEndpoint['mime_type'] || $restEndpoint['mime_type'] == 'application/json';
        if ($payloadIsJson) {
            try {
                $json = json_encode($restEndpoint['json_body_template'], JSON_THROW_ON_ERROR);
                $body = Template::renderTemplateToString($json, [$parameters]);
            } catch (JsonException $e) {
                throw new \Exception('default case not implemented', 0, $e);
            }
        } else {
            throw new \Exception('default case not implemented');
        }
        $url = $restEndpoint['url'];
        $httpClient = new HttpClient();
        $method = strtoupper($restEndpoint['method']);
        return $httpClient->{"http$method"}($url . $urlParameter, $body, $prePostCallback);
    }//end restRequestReply()

    /**
     * @param string $bodyTemplate
     * @param array $parameters
     *
     * @return string
     *
     * @since version
     */
    static function urlEncodedBody(string $bodyTemplate, array $parameters): string
    {
        $body = '';
        foreach ($bodyTemplate as $bodyKey => $bodyValue) {
            $bodyElt = Template::renderTemplateToString($bodyKey . '=' . $bodyValue, [$parameters]);
            if (empty($body) === false) {
                $body .= '&';
            }
            $body .= $bodyElt;
        }
        return $body;
    }//end stdHttpBody()

}
