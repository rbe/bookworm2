<?php
/**
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

error_reporting(E_ALL);
ini_set('display_errors', 'On');

//
// Joomla
//

define('JOOMLA_HOME', __DIR__ . '/..');
define('_JEXEC', 1);
if (file_exists(JOOMLA_HOME . '/defines.php')) {
    include_once JOOMLA_HOME . '/defines.php';
}
if (!defined('_JDEFINES')) {
    define('JPATH_BASE', JOOMLA_HOME);
    require_once JPATH_BASE . '/includes/defines.php';
}
require_once JPATH_BASE . '/includes/framework.php';
$mainframe = JFactory::getApplication('site');
$mainframe->initialise();

/**
 * Get a value for a field from comprofiler's database for an user.
 * @param $user
 * @param $field
 * @return string
 */
function getUserValueFromComprofiler($user, $field)
{
    $db = JFactory::getDBO();
    $query = 'SELECT ' . $field . ' FROM #__comprofiler WHERE user_id=' . $user->id;
    $db->setQuery($query);
    return $db->loadResult();
}

/**
 * Get user's data.
 * @return null|string
 */
function getHoerernummer()
{
    $user = JFactory::getUser();
    if (isset($user)) {
        $hnr = getUserValueFromComprofiler($user, 'cb_hoerernummer');
        if (isset($hnr)) {
            return $hnr;
        }
    }
    return null;
}

//
// Proxy
//

include_once 'autoload.php';

use ApProxy\AppInfo;
use ApProxy\ApProxyFactory;

/**
 * Redirect if no user is logged in.
 * @return bool
 */
function sendHttpRedirectIfNoUser()
{
    $user = JFactory::getUser();
    $userOk = isset($user) && $user->id > 0;
    if ($userOk) {
        return false;
    } else {
        ApProxy\HttpHelper::sendHttpRedirectWithStatus('NO_USER');
        return true;
    }
}

/**
 * @param string $parsedRequestUri
 * @param array $params
 * @return string
 */
function removeQueryParameter($query, $params)
{
    parse_str($query, $queryParameters);
    $filtered = array_filter($queryParameters, function ($k) {
        return $k !== 'hnr';
    }, ARRAY_FILTER_USE_KEY);
    return http_build_query($filtered);
}

/**
 * Proxy request to application.
 */
function proxyRequestToApp()
{
    ApProxyFactory::configure(new AppInfo('hoerbuchkatalog',
        'http://hoerbuchkatalog:8080', '/hoerbuchkatalog'));
    $approxy = ApProxyFactory::create($_SERVER['REQUEST_URI']);
    if (isset($approxy)) {
        $customizeUriDelegate = function ($appInfo, $requestUri) {
            $hnr = getHoerernummer();
            $parsedUri = parse_url($requestUri) or die('Cannot parse URL');
            if (isset($parsedUri['query'])) {
                $filteredQuery = removeQueryParameter($parsedUri['query'], ['hnr']);
                $appUri = $parsedUri['path'] . '?' . $filteredQuery;
            } else {
                $appUri = $requestUri;
            }
            if (isset($hnr)) {
                if (isset($parsedUri['query'])) {
                    $appUri .= '&';
                } else {
                    $appUri .= '?';
                }
                $appUri .= 'hnr=' . $hnr;
            }
            //echo $appUri; return;
            return $appUri;
        };
        /*
        $requestUri = $_SERVER['REQUEST_URI'];
        if (strpos($requestUri, "nutzerbereich") > 0) {
            sendHttpRedirectIfNoUser() or $approxy->perform($customizeUriDelegate);
        } else
        */
        $approxy->perform($customizeUriDelegate);
    } else {
        ApProxy\HttpHelper::sendHttpRedirectWithStatus('NO_APPROXY');
    }
}

proxyRequestToApp();

?>
