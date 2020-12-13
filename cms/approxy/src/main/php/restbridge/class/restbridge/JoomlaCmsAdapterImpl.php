<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

use Exception;
use JFactory;
use Joomla;
use Joomla\CMS\Factory;
use Joomla\CMS\Helper\ModuleHelper;
use stdClass;

class JoomlaCmsAdapterImpl implements CmsAdapter
{

    const ONE_DAY = 60 * 60 * 24;

    const ONE_WEEK = self::ONE_DAY * 7;


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
        } catch (Exception $e) {
            restBridgeErrorLog('Exception while retrieving JInput: ' . $e->getMessage());
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
     * @param array $meta
     * @param array $rowsWithValues Description.
     *
     * @return string
     *
     * @since 1.0
     */
    public function renderTemplate(string $customTemplateModuleName, array $meta, array $rowsWithValues): string
    {
        $customTemplateModule = self::customModule($customTemplateModuleName);
        if (is_null($customTemplateModule) === false) {
            $template = new Template($customTemplateModule->content);
            return $template->renderToString($meta, $rowsWithValues);
        } else {
            return '';
        }

    }//end renderTemplate()


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
        $customModuleExistsAndHasContent = is_null($customModule) === false
            && is_object($customModule) === true
            && empty($customModule->content) === false;
        if ($customModuleExistsAndHasContent === true) {
            restBridgeDebugLog('Custom module "' . $customModuleName
                . '" title="' . $customModuleTitle . '" exists and has content');
            return $customModule;
        } else {
            restBridgeDebugLog('Custom module "' . $customModuleName
                . '" title="' . $customModuleTitle . '" not found');
            return null;
        }

    }//end getModuleContent()


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
            restBridgeWarningLog('Module "' . $customModuleName . '" has no content');
            return '';
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
            if (isset($value) === true) {
                $value = $result;
            } else {
                restBridgeWarningLog('User [' . $user->id . ']: no value for field ' . $field);
            }
        }

        if (isset($value) === false) {
            $value = '';
        }

        return $value;

    }//end getUserValue()


    /**
     * Description.
     *
     * @param string $name Comment.
     *
     * @return string Comment.
     *
     * @throws Exception Comment.
     *
     * @since 1.0
     */
    public function getCookie(string $name): string
    {
        $app = JFactory::getApplication();
        $cookie = $app->input->cookie;
        return $cookie->get($name, '');
    }//end getCookie()


    /**
     * Description.
     *
     * @param string $name Comment.
     * @param string $value Comment.
     *
     * @throws Exception Comment.
     *
     * @since 1.0
     */
    public function setCookieOnce(string $name, string $value): void
    {
        $app = JFactory::getApplication();
        $cookie = $app->input->cookie;
        $currentValue = $cookie->get($name, null);
        if (is_null($currentValue)) {
            $time = time() + self::ONE_WEEK;
            $cookie->set($name, $value, $time,
                $app->get('cookie_path', '/'),
                $app->get('cookie_domain'),
                $app->isSSLConnection());
        }
    }//end setCookieOnce()


}//end class
