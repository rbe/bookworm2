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
        $result = $this->replaceData($rowsWithValues);
        return $this->replaceMeta($result, $meta);

    }//end renderToString()


    private function replaceMeta(string $content, array $meta): string
    {
        preg_match_all('/\{meta.([A-Za-z0-9_]+)*\}/', $this->template, $matches);
        foreach ($matches[1] as $match) {
            $content = str_replace('{meta.' . $match . '}', $meta[$match], $content);
        }
        return $content;

    }//end replaceMeta()


    private function replaceData(array $rowsWithValues): string
    {
        preg_match_all('/\{([A-Za-z0-9_]+)*\}/', $this->template, $matches);
        $content = '';
        foreach ($rowsWithValues as $row) {
            $c = $this->template;
            foreach ($matches[1] as $match) {
                $key = '{' . $match . '}';
                if (array_key_exists($match, $row) === true) {
                    $value = $this->value($row[$match]);
                } else {
                    restBridgeDebugLog('No value found for "' . $key . '" in ' . print_r($row, true));
                    $value = '';
                }

                $c = str_replace($key, $value, $c);
            }

            $content .= $c;
        }
        return $content;

    }//end replaceData()


    /**
     * Description.
     *
     * @param string $rowValue Comment.
     *
     * @return string Comment.
     *
     * @since 1.0
     */
    private function value(string $rowValue): string
    {
        if (is_bool($rowValue) === true) {
            if ($rowValue === true) {
                $value = 'true';
            } else {
                $value = 'false';
            }

        } else {
            $value = $rowValue;
        }

        return $value;

    }//end value()


}//end class
