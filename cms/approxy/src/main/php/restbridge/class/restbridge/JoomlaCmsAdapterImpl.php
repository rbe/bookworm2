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
use JInput;
use Joomla;
use Joomla\CMS\Factory;
use Joomla\CMS\Helper\ModuleHelper;
use stdClass;

class JoomlaCmsAdapterImpl implements CmsAdapter
{

    const MIN_15 = 15 * 60;

    const ONE_DAY = 60 * 60 * 24;

    const ONE_WEEK = self::ONE_DAY * 7;


    /**
     * Description.
     *
     * @param array $array Comment.
     *
     * @return array
     *
     * @throws Exception
     *
     * @since 1.0
     */
    public function resolveParameters(array $array): array
    {
        $input = $this->getJInput();
        if (isset($input) === true) {
            foreach ($array as $key => $value) {
                if ($value === 'HttpRequest') {
                    $array[$key] = $input->get($key, '', 'STRING');
                } else if (strpos($value, 'Cookie') > 0) {
                    $strings = explode('-', $value);
                    $cookieName = $strings[1];
                    $array[$key] = $this->getCookie($cookieName);
                }
            }
            restBridgeDebugLog('resolveParameters: ' . print_r($array, true));
        } else {
            restBridgeErrorLog('resolveParameters: Failed');
        }

        return $array;
    }//end resolveParameters()


    /**
     * Description.
     *
     * @param string $customTemplateModuleName Comment.
     * @param array $meta Comment.
     * @param array $rowsWithValues Comment.
     * @param string $default Comment.
     *
     * @return string
     *
     * @since 1.0
     */
    public function renderTemplate(string $customTemplateModuleName, array $meta, array $rowsWithValues, string $default = ''): string
    {
        $customTemplateModule = self::customModule($customTemplateModuleName);
        if (is_null($customTemplateModule) === false) {
            $template = new Template($customTemplateModule->content);
            return $template->renderToString($meta, $rowsWithValues);
        } else {
            return $default;
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
            restBridgeDebugLog('customModule: "' . $customModuleName
                . '" title="' . $customModuleTitle . '" exists and has content');
            return $customModule;
        } else {
            restBridgeWarningLog('customModule: "' . $customModuleName
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
            return $customModule->content;
        } else {
            restBridgeWarningLog('getModuleContent: Custom module "' . $customModuleName . '" not found');
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
                restBridgeWarningLog('getUserValue(' . $user->id . '): no value for field ' . $field);
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
    public function setCookie(string $name, string $value): void
    {
        $app = JFactory::getApplication();
        $cookie = $app->input->cookie;
        $time = time() + self::MIN_15;
        $cookie->set($name, $value, $time,
            $app->get('cookie_path', '/'),
            $app->get('cookie_domain'),
            $app->isSSLConnection(),
            false);
    }//end setCookieOnce()


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
        if (is_null($currentValue) === true) {
            $this->setCookie($name, $value);
        }
    }//end setCookieOnce()


    private function getJInput(): ?JInput
    {
        try {
            $app = Factory::getApplication();
            return $app->input;
        } catch (Exception $e) {
            restBridgeErrorLog('getJInput: Exception while retrieving JInput: ' . $e->getMessage());
            return null;
        }
    }//end getJInput()


}//end class
