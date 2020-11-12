<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

namespace restbridge;

final class Template
{

    /**
     * Render a template with values.
     *
     * @param string $template The template.
     * @param string[][] $rowsWithValues Rows with values, e.g.
     *                      Array (
     *                         [0] => Array (
     *                                [0] => {titelnummer}
     *                                [1] => {name}
     *                                )
     *                         [1] => Array (
     *                                [0] => titelnummer
     *                                [1] => name
     *                                )
     *                      )
     *
     * @return string Content: template with {placeholder} substituted with value.
     */
    public static function renderTemplateToString(string $template, array $rowsWithValues): string
    {
        preg_match_all('/\{([A-Za-z_]+)*\}/', $template, $matches);
        $matches = $matches[1];
        $content = '';
        foreach ($rowsWithValues as $row) {
            $c = $template;
            foreach ($matches as $match) {
                $key = '{' . $match . '}';
                if (array_key_exists($match, $row) === true) {
                    $value = $row[$match];
                } else {
                    error_log('No value found for ' . $key, 0);
                    $value = '';
                }
                $c = str_replace($key, $value, $c);
            }
            $content .= $c;
        }
        return $content;
    }//end renderTemplate()

    /**
     * Analyze parameter string.
     *
     * @param string $commandName Name of command.
     * @param string $parameterString Parameters, form: name:value.
     * @param array $messages Messages.
     *
     * @return string[][]
     */
    public static function analyzeParameters(string $commandName, string $parameterString, array &$messages): array
    {
        // [2] => titelnummer:12345,name:Ralf
        // [0] => titelnummer:12345
        // [1] => name:Ralf
        /* @var $parameters string[] */
        $parameters = explode(',', $parameterString);
        /* @var $parameterArray string[][] */
        $parameterArray = [];
        foreach ($parameters as $p) {
            $keyValue = explode(':', $p);
            if (isset($keyValue) === true && count($keyValue) === 2) {
                $key = $keyValue[0];
                $value = $keyValue[1];
                $parameterArray[$key] = $value;
            } else {
                $messages[] = 'Command ' . $commandName . ': Error analyzing parameter ' . $keyValue;
            }
        }
        return $parameterArray;
    }//end analyzeParameters()

}
