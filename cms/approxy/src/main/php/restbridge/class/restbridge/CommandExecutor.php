<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class CommandExecutor
{

    /**
     * Description.
     *
     * @var CmsAdapter The CMS adapter.
     *
     * @since 1.0
     */
    private CmsAdapter $cmsAdapter;

    /**
     * Description.
     *
     * @var CommandExecutorClassHelper The class helper.
     *
     * @since 1.0
     */
    private CommandExecutorClassHelper $classHelper;


    /**
     * CommandExecutor constructor.
     *
     * @param CmsAdapter $cmsAdapter The CMS adapter.
     *
     * @since 1.0
     */
    public function __construct(CmsAdapter $cmsAdapter)
    {
        $this->cmsAdapter = $cmsAdapter;
        $this->classHelper = new CommandExecutorClassHelper();
    }//end __construct()


    /**
     * Analyze parameter string.
     *
     * @param string $commandName Name of command.
     * @param string $parameterString Parameters, form: name:value.
     *
     * @return string[][]
     * @since  version
     */
    public function analyzeParameters(string $commandName, string $parameterString): array
    {
        $parameters = explode(',', $parameterString);
        $parameterArray = [];
        foreach ($parameters as $p) {
            $keyValue = explode(':', $p);
            $isKeyWithValue = isset($keyValue) === true && count($keyValue) === 2;
            if ($isKeyWithValue) {
                $key = $keyValue[0];
                $value = $keyValue[1];
                $parameterArray[$key] = $value;
            } else {
                // TODO param:, -> no value -> lookup per JoomlaCmsAdapter/JInput
                error_log('Command ' . $commandName . ': Error analyzing parameter ' . print_r($p, true));
            }
        }

        return $parameterArray;

    }//end analyzeParameters()


    /**
     * Execute a command.
     *
     * @param string $commandName Command name.
     * @param string $parameters Parameters.
     *
     * @return array Result of command execution.
     *
     * @since 1.0
     */
    public function executeCommand(string $commandName, string $parameters): array
    {
        $parameterArray = $this->analyzeParameters($commandName, $parameters);
        error_log('Parameters before JInput: ' . print_r($parameterArray, true), 0);
        $parameterArray = $this->cmsAdapter->resolveParameters($parameterArray);
        error_log('Parameters after JInput: ' . print_r($parameterArray, true), 0);
        global $restBridge;
        /** @var $restEndpoint array */
        $restEndpoint = $restBridge['REST_ENDPOINTS'][$commandName];
        if (isset($restEndpoint) === false) {
            $result = 'CommandExecutor#executeCommand: REST endpoint ' . $commandName . ' not found';
            error_log($result, 0);
            return ['error' => $result];
        }

        $headers = $restEndpoint['headers'];
        foreach ($headers as $k => $v) {
            $template = new Template($v);
            $headers[$k] = $template->renderToString([$parameterArray]);
        }
        error_log('CommandExecutor#executeCommand: HTTP Headers: '
            . print_r($headers, true), 0);
        /** @var $requestDto array */
        $requestDto = $restEndpoint['requestDto'];
        if (isset($requestDto) === false) {
            $requestDto = $GLOBALS['restBridge']['REQUEST_DTOS'][$commandName];
        }
        if (isset($requestDto) === false) {
            $requestDto = [];
        } else {
            foreach ($requestDto as $k => $v) {
                $template = new Template($v);
                $requestDto[$k] = $template->renderToString([$parameterArray]);
            }
        }
        $command = new RestRequestReplyCommandImpl(
            [
                'endpoint' => $restEndpoint,
                'requestDto' => $requestDto,
                'urlParameters' => $parameterArray,
                'preHttpPostCallback' => function ($ch) use ($headers) {
                    curl_setopt(
                        $ch,
                        CURLOPT_HTTPHEADER,
                        ArrayHelper::arrayCombineKeyValue($headers)
                    );
                },
            ]
        );
        return $command->execute();

    }//end executeCommand()


}//end class
