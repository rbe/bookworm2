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
     * @param array $standardValues Standard values for templates.
     *
     * @return string Content: template with {placeholder} substituted with value.
     *
     * @since  1.0
     */
    public function renderToString(array $meta, array $rowsWithValues, array $standardValues = []): string
    {
        restBridgeTraceLog('$this->template=' . $this->template);
        restBridgeTraceLog('$rowsWithValues=' . print_r($rowsWithValues, true));
        $contentAfterMeta = empty($meta) === false
            ? $this->replaceMeta($this->template, $meta)
            : $this->template;
        restBridgeTraceLog('$contentAfterMeta = ' . $contentAfterMeta);
        $contentAfterData = empty($rowsWithValues) === false
            ? $this->replaceData($contentAfterMeta, $rowsWithValues)
            : $contentAfterMeta;
        restBridgeTraceLog('$contentAfterData = ' . $contentAfterData);
        $contentAfterStdVal = empty($standardValues) === false
            ? $this->replaceData($contentAfterData, $standardValues)
            : $contentAfterData;
        restBridgeTraceLog('$contentAfterStdVal = ' . $contentAfterStdVal);
        $content = empty($contentAfterStdVal) === false ? $contentAfterStdVal : $contentAfterMeta;
        restBridgeTraceLog('$content = ' . $content);
        return $this->cleanup($content);

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
                    restBridgeTraceLog('replaceData: Key ' . $key . '=' . print_r($value, true));
                    if (isset($value) && empty($value) === false) {
                        $content = str_replace($key, $value, $content);
                    } else {
                        restBridgeTraceLog('replaceData: No value for key "' . $key . '" found in ' . print_r($row, true));
                    }
                } else {
                    restBridgeTraceLog('replaceData: Key "' . $key . '" not found in ' . print_r($row, true));
                }
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


    private function cleanup(string $template): string
    {
        preg_match_all('/\{([A-Za-z0-9_]+)*\}/', $template, $matches);
        $content = $template;
        foreach ($matches[1] as $match) {
            $key = '{' . $match . '}';
            $content = str_replace($key, '', $content);
        }

        return $content;

    }//end cleanup()


}//end class
