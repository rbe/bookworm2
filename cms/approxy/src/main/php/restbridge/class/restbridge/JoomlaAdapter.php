<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

namespace restbridge;

use Joomla;

class JoomlaAdapter
{

    /**
     * Retrieve custom Joomla module.
     *
     * @param string $commandName Name of command.
     * @param array $messages Messages.
     *
     * @return stdClass
     */
    static function customModule(string $commandName, array &$messages): stdClass
    {
        /* @var $customModuleTitle string */
        $customModuleTitle = 'Bookworm_' . $commandName;
        /* @var $customModule stdClass */
        $customModule = Joomla\CMS\Helper\ModuleHelper::getModule('mod_custom', $customModuleTitle);
        /* @var $customModuleExists bool */
        $customModuleExists = isset($customModule) === true
            && is_object($customModule) === true
            && empty($customModule->content) === false;
        if ($customModuleExists === false) {
            $messages[] = 'Command ' . $commandName . ': Custom module title=' . $customModuleTitle . ' not found';
        }
        return $customModule;
    }//end customModule()

    /**
     * Put values into placeholders in template.
     *
     * @param stdClass $customTemplateModule The module = template.
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
    static function renderCustomTemplateModule(stdClass $customTemplateModule, array $rowsWithValues): string
    {
        return Template::renderTemplateToString($customTemplateModule->content, $rowsWithValues);
    }//end renderCustomTemplateModule()

}
