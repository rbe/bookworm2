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
     * @return object
     *
     * @since 1.0
     */
    public function execute(): array
    {
        $response = null;
        try {
            $restReqResp = new RestRequestReply($this->parameters['endpoint'], $this->parameters['requestDto']);
            $response = $restReqResp->execute(
                $this->parameters['urlParameters'],
                $this->parameters['preHttpPostCallback']
            );
            $responseBody = $response->getJson();
        } catch (\Exception $e) {
            error_log('Exception: ' . $e->getMessage(), 0);
            $responseBody = [];
        }

        return $responseBody;

    }//end execute()


}//end class
