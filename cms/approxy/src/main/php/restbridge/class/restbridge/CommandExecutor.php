<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

use Exception;

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
     * Execute a command.
     *
     * @param string $commandName Command name.
     * @param string $urlParameters Parameters.
     *
     * @return CommandResult Result of command execution.
     *
     * @throws Exception Comment.
     *
     * @since 1.0
     */
    public function executeCommand(string $commandName, string $urlParameters): CommandResult
    {
        global $restBridge;
        /** @var $restEndpoint array */
        $restEndpoint = $restBridge['REST_ENDPOINTS'][$commandName];
        if (isset($restEndpoint) === false) {
            $result = 'CommandExecutor#executeCommand: REST endpoint ' . $commandName . ' not found';
            restBridgeErrorLog($result);
            return new CommandResult(500, [], []);
        }

        $urlParameterArray = $this->resolveHttpRequestParameters($commandName, $urlParameters);
        $headers = $restEndpoint['headers'];
        $this->resolveTemplateHttpHeaders($headers, $urlParameterArray);
        if (array_key_exists($commandName, $restBridge['REQUEST_DTOS'])) {
            $requestDto = $this->resolveRequestDto($restBridge['REQUEST_DTOS'][$commandName], $urlParameterArray);
        } else {
            $requestDto = [];
        }

        // Encode URL parameters according to RFC 3986
        foreach ($urlParameterArray as $k => $v) {
            $urlParameterArray[$k] = rawurlencode($v);
        }
        $command = new RestRequestReplyCommandImpl(
            [
                'endpoint' => $restEndpoint,
                'requestDto' => $requestDto,
                'urlParameters' => $urlParameterArray,
                'preHttpPostCallback' => function ($ch) use ($headers) {
                    curl_setopt(
                        $ch,
                        CURLOPT_HTTPHEADER,
                        ArrayHelper::arrayCombineKeyValue($headers)
                    );
                },
            ]
        );
        $result = $command->execute();
        if ($result->isOk()) {
            // Merge urlParameterArray into every row
            $jsonHelper = new JsonHelper($result->getData());
            $merged = $jsonHelper->merge($urlParameterArray);
            $result->updateData($merged);
        } else {
            // ignore
        }

        return $result;

    }//end executeCommand()


    /**
     * Description.
     *
     * @param string $commandName Comment.
     * @param string $parameters Comment.
     *
     * @return array Comment.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    private function resolveHttpRequestParameters(string $commandName, string $parameters): array
    {
        $parameterArray = $this->analyzeParameters($commandName, $parameters);
        $parameterArray = $this->cmsAdapter->resolveParameters($parameterArray);
        restBridgeDebugLog('resolveHttpRequestParameters: ' . print_r($parameterArray, true));
        return $parameterArray;
    }//end analyzeParameters()


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
                restBridgeErrorLog('analyzeParameters: Command ' . $commandName
                    . ': Error analyzing parameter ' . print_r($p, true));
            }
        }

        return $parameterArray;

    }//end resolveHttpRequestParameters()


    /**
     * Description.
     *
     * @param array $headers Comment.
     * @param array $urlParameterArray Comment.
     *
     * @return void
     *
     * @since 1.0
     */
    private function resolveTemplateHttpHeaders(array &$headers, array &$urlParameterArray): void
    {
        foreach ($headers as $key => $value) {
            $template = new Template($value);
            $headers[$key] = $template->renderToString([], [$urlParameterArray]);
        }
        restBridgeDebugLog('resolveTemplateHttpHeaders: ' . print_r($headers, true));
    }//end resolveTemplateHttpHeaders()


    /**
     * Description.
     *
     * @param $requestDto Comment.
     * @param $restBridge Comment.
     * @param $parameterArray Comment.
     *
     * @return array Comment.
     *
     * @since 1.0
     */
    private function resolveRequestDto(array $requestDto, array $parameterArray): array
    {
        if (isset($requestDto) === false) {
            $requestDto = [];
        } else {
            foreach ($requestDto as $key => $value) {
                $template = new Template($value);
                $requestDto[$key] = $template->renderToString([$parameterArray]);
            }
        }

        return $requestDto;
    }//end resolveRequestDto()


}//end class
