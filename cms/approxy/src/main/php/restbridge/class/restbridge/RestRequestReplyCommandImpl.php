<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class RestRequestReplyCommandImpl extends AbstractCommand
{


    /**
     * Execute this command.
     *
     * @return array
     *
     * @since 1.0
     */
    public function execute(): array // TODO Return CommandResult
    {
        $restEndpoint = $this->parameters['endpoint'];
        if (isset($restEndpoint) === false) {
            restBridgeDebugLog('Cannot execute, no REST endpoint');
            return [];
        }

        $requestDto = $this->parameters['requestDto'];
        if (isset($requestDto) === false) {
            $requestDto = [];
        }

        /*restBridgeDebugLog('RestRequestReplyCommandImpl#execute:'
            . ' restEndpoint=' . print_r($restEndpoint, true)
            . ' requestDto=' . print_r($requestDto, true));*/
        $responseBody = [];
        try {
            $restReqResp = new RestRequestReply($restEndpoint, $requestDto);
            $httpResponse = $restReqResp->execute(
                $this->parameters['urlParameters'],
                $this->parameters['preHttpPostCallback']
            );
            $statusCode = $httpResponse->getStatusCode();
            if ($statusCode >= 200 && $statusCode < 300) {
                $responseBody = $httpResponse->getJson();
                restBridgeDebugLog('Response: HTTP ' . $statusCode . ' Body=' . print_r($responseBody, true));
            }
        } catch (\Exception $e) {
            restBridgeDebugLog('Exception: ' . $e->getMessage());
        }

        return $responseBody;

    }//end execute()


}//end class
