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
 * @since       version
 */
interface CmsAdapter
{

    /**
     * Put values into placeholders in template.
     *
     * @param string $customTemplateModuleName The module = template.
     * @param string[][] $rowsWithValues Rows with values.
     *
     * @return string Content: template with {placeholder} substituted with value.
     *
     * @since  version
     */
    public function renderTemplate(string $customTemplateModuleName, array $meta, array $rowsWithValues): string;

    /**
     * Just use a module for content.
     *
     * @param string $customModuleName The module = template.
     * @return string Content: template with {placeholder} substituted with value.
     *
     * @since  version
     */
    public function getModuleContent(string $customModuleName): string;

    /**
     * Get user's data.
     *
     * @param string $field
     *
     * @return string
     * @since  version
     */
    public function getUserValue(string $field): string;

    public function getCookie(string $name): string;

    public function setCookie(string $name, string $value): void;

    public function setCookieOnce(string $name, string $value): void;

}
