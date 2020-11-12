<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

namespace restbridge;

final class Commands
{

    /**
     * Execute a command.
     *
     * @param array $matches Command name and parameters.
     *
     * @return string Content.
     */
    function executeCommand(array $matches): string
    {
        $messages = [];
        // [0] => { Bookworm : HalloChristian titelnummer:12345,name:Ralf }
        // [1] => HalloChristian
        // [2] => titelnummer:12345,name:Ralf
        /* @var $commandName string */
        $commandName = $matches[1];
        /* @var $parameterArray string[][] */
        $parameterArray = Template::analyzeParameters($commandName, $matches[2], $messages);
        if ($messages === true) {
            return implode("<br>\n", $messages);
        }
        /* @var $result array */
        $classHelper = new ClassHelper();
        $result = $classHelper->callMethod($commandName, $parameterArray, $this);
        $hasMergableResult = is_array($result) === true && empty($result) === false;
        $content = '';
        if ($hasMergableResult) {
            if (array_key_exists('message', $result) === true) {
                $content = $result['message'];
            } else {
                $customModule = JoomlaAdapter::customModule($commandName, $messages);
                $content = JoomlaAdapter::renderCustomTemplateModule($customModule, $result);
            }
        }
        return $content;
    }//end executeCommand()

    /**
     * Execute command HeyChristian.
     *
     * @param array $parameters Parameters.
     *
     * @return string[][] Result rows with values.
     */
    private function cmdHeyChristian(array $parameters): array
    {
        return [
            [
                'titelnummer' => '1__' . $parameters['titelnummer'] . '__081542',
                'name' => '1__Christian'
            ],
            [
                'titelnummer' => '2__' . $parameters['titelnummer'] . '__081542',
                'name' => '2__' . $parameters['name']
            ]
        ];
    }// end cmdHeyChristian()

}
