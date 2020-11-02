<?php
defined('_JEXEC') or die('Restricted access');

require_once dirname(__FILE__) . '/helper.php';

$hello = modHelloWorldHelper::getHello($params);

$layout = $params->get('layout', 'default');
require JModuleHelper::getLayoutPath('mod_backendmodul', $layout);
