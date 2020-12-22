<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

use Exception;

final class RestRequestReplyCommandImpl extends AbstractCommand
{

    private array $restEndpoint;

    private array $requestDto;


    /**
     * RestRequestReplyCommandImpl constructor.
     *
     * @param array $parameters
     *
     * @throws Exception Comment.
     *
     * @since 1.0
     */
    public function __construct(array $parameters)
    {
        parent::__construct($parameters);
        if (isset($this->parameters['endpoint']) === false) {
            throw new Exception('Cannot execute, no REST endpoint');
        }
        $this->restEndpoint = $this->parameters['endpoint'];
        if (isset($this->parameters['requestDto']) === false) {
            $this->requestDto = [];
        }
        $this->requestDto = $this->parameters['requestDto'];
    }//end __construct()


    /**
     * Execute this command.
     *
     * @return CommandResult Comment.
     *
     * @since 1.0
     */
    public function execute(): CommandResult
    {
        try {
            $restReqResp = new RestRequestReply($this->restEndpoint, $this->requestDto);
            $httpResponse = $restReqResp->execute(
                $this->parameters['urlParameters'],
                $this->parameters['preHttpPostCallback']
            );
            $reply = $httpResponse->getJson();
            return new CommandResult($httpResponse->getStatusCode(), $reply['meta'] ?? [], $reply['data'] ?? []);
        } catch (Exception $e) {
            restBridgeErrorLog('execute: Exception: ' . $e->getMessage());
            return new CommandResult(500, [], []);
        }
    }//end execute()


}//end class
