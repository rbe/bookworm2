<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

use JFactory;
use Joomla;
use Joomla\CMS\Factory;
use Joomla\CMS\Helper\ModuleHelper;
use stdClass;

class JoomlaCmsAdapterImpl implements CmsAdapter
{


    /**
     * Description.
     *
     * @param array $array Comment.
     *
     * @return array
     *
     * @since 1.0
     */
    public function resolveParameters(array $array): array
    {
        try {
            $app = Factory::getApplication();
            $input = $app->input;
        } catch (\Exception $e) {
            restBridgeDebugLog('Exception while retrieving JInput: ' . $e->getMessage());
        }
        if (isset($input) === true) {
            foreach ($array as $key => $value) {
                if ($value === 'HttpRequest') {
                    $array[$key] = $input->get($key, '', 'ALNUM');
                }

            }
        } else {
            restBridgeDebugLog('Could not get JInput');
        }

        return $array;
    }//end resolveParameters()


    /**
     * Description.
     *
     * @param string $customTemplateModuleName Description.
     * @param array $rowsWithValues Description.
     *
     * @return string
     *
     * @since 1.0
     */
    public function renderTemplate(string $customTemplateModuleName, array $rowsWithValues): string
    {
        $customTemplateModule = self::customModule($customTemplateModuleName);
        if (is_null($customTemplateModule) === false) {
            $template = new Template($customTemplateModule->content);
            //restBridgeDebugLog('Rendering values into template: ' . print_r($rowsWithValues, true));
            return $template->renderToString($rowsWithValues);
        } else {
            restBridgeDebugLog('Template "' . $customTemplateModuleName . '" not found');
            return '';
        }

    }//end renderTemplate()


    /**
     * Description.
     *
     * @param string $customModuleName Description.
     *
     * @return string
     *
     * @since 1.0
     */
    public function getModuleContent(string $customModuleName): string
    {
        $customModule = self::customModule($customModuleName);
        if (is_null($customModule) === false) {
            restBridgeDebugLog('Returning module content of "' . $customModuleName . '"');
            return $customModule->content;
        } else {
            restBridgeDebugLog('Module "' . $customModuleName . '" has no content');
            return '';
        }

    }//end getModuleContent()


    /**
     * Retrieve custom Joomla module.
     *
     * @param string $customModuleName Name of custom module.
     *
     * @return stdClass|null
     *
     * @since 1.0
     */
    private static function customModule(string $customModuleName): ?stdClass
    {
        global $restBridge;
        $customModuleTitle = $restBridge['TEMPLATE_NAME_PREFIX'] . '_' . $customModuleName;
        $customModule = ModuleHelper::getModule('mod_custom', $customModuleTitle);
        restBridgeDebugLog('$customModule=' . print_r($customModule, true));
        $customModuleExistsAndHasContent = /*isset($customModule) === true
            &&*/ is_null($customModule) === false
            && is_object($customModule) === true
            && empty($customModule->content) === false;
        if ($customModuleExistsAndHasContent === true) {
            restBridgeDebugLog('Custom module "' . $customModuleName . '" title="' . $customModuleTitle . '" exists and has content');
            return $customModule;
        } else {
            restBridgeDebugLog('Custom module "' . $customModuleName . '" title="' . $customModuleTitle . '" not found');
            return null;
        }

    }//end getModuleContent()


    /**
     * Description.
     *
     * @param string $field Description.
     *
     * @return string
     *
     * @since 1.0
     */
    public function getUserValue(string $field): string
    {
        $value = '';
        $user = JFactory::getUser();
        if (isset($user) === true) {
            $db = JFactory::getDBO();
            $query = 'SELECT ' . $field . ' FROM #__comprofiler WHERE user_id=' . $user->id;
            $db->setQuery($query);
            $result = $db->loadResult();
            restBridgeDebugLog('User [' . $user->id . ']: field ' . $field . ' has value [' . $result . ']');
            if (isset($value) === true) {
                $value = $result;
            } else {
                restBridgeDebugLog('User [' . $user->id . ']: no value for field ' . $field);
            }
        }

        if (isset($value) === false) {
            $value = '';
        }

        return $value;

    }//end getUserValue()


}//end class
