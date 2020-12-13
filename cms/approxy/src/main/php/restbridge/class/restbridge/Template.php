<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

/**
 * @package     restbridge
 *
 * @since 1.0
 */
final class Template
{

    /**
     * @var   string
     * @since 1.0
     */
    private string $template;


    /**
     * Template constructor.
     *
     * @param string $template The template.
     *
     * @since 1.0
     */
    public function __construct(string $template)
    {
        $this->template = $template;

    }//end __construct()


    /**
     * Render a template with values.
     *
     * @param array $meta Meta data.
     * @param array $rowsWithValues Rows with values.
     *
     * @return string Content: template with {placeholder} substituted with value.
     * @since  version
     */
    public function renderToString(array $meta, array $rowsWithValues): string
    {
        $contentAfterMeta = $this->replaceMeta($this->template, $meta);
        $contentAfterData = $this->replaceData($contentAfterMeta, $rowsWithValues);
        return empty($contentAfterData) === false ? $contentAfterData : $contentAfterMeta;

    }//end renderToString()


    private function replaceMeta(string $content, array $meta): string
    {
        preg_match_all('/\{meta.([A-Za-z0-9_]+)*\}/', $content, $matches);
        foreach ($matches[1] as $match) {
            $content = str_replace('{meta.' . $match . '}', $meta[$match], $content);
        }
        return $content;

    }//end replaceMeta()


    private function replaceData(string $template, array $rowsWithValues): string
    {
        preg_match_all('/\{([A-Za-z0-9_]+)*\}/', $template, $matches);
        $ret = '';
        foreach ($rowsWithValues as $row) {
            $content = $template;
            foreach ($matches[1] as $match) {
                $key = '{' . $match . '}';
                if (array_key_exists($match, $row) === true) {
                    $value = $this->value($row, $match);
                } else {
                    restBridgeDebugLog('No value found for "' . $key . '" in ' . print_r($row, true));
                    $value = '';
                }

                $content = str_replace($key, $value, $content);
            }

            $ret .= $content;
        }
        return $ret;

    }//end replaceData()


    private function value(array $row, string $match): string
    {
        if (is_bool($row[$match]) === true) {
            if ($row[$match] === true) {
                $value = 'true';
            } else {
                $value = 'false';
            }

        } else {
            $value = $row[$match];
        }

        return $value;

    }//end value()


}//end class
