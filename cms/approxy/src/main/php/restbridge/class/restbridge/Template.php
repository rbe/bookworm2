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
     * @param array $rowsWithValues Rows with values.
     *
     * @return string Content: template with {placeholder} substituted with value.
     * @since  version
     */
    public function renderToString(array $rowsWithValues): string
    {
        preg_match_all('/\{([A-Za-z_]+)*\}/', $this->template, $matches);
        $matches = $matches[1];
        $content = '';
        foreach ($rowsWithValues as $row) {
            $c = $this->template;
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

    }//end renderToString()

}//end class
