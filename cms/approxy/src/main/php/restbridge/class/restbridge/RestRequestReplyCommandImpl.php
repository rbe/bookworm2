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
    public function execute(): array
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

        $response = null;
        restBridgeDebugLog('RestRequestReplyCommandImpl#execute:'
            .' restEndpoint='.print_r($restEndpoint, true)
            .' requestDto='.print_r($requestDto, true));
        try {
            $restReqResp = new RestRequestReply($restEndpoint, $requestDto);
            $response = $restReqResp->execute(
                $this->parameters['urlParameters'],
                $this->parameters['preHttpPostCallback']
            );
            $responseBody = $response->getJson();
            restBridgeDebugLog('Response: '.print_r($responseBody, true));
        } catch (\Exception $e) {
            restBridgeDebugLog('Exception: ' . $e->getMessage());
            $responseBody = [];
        }

        return $responseBody;

    }//end execute()


}//end class