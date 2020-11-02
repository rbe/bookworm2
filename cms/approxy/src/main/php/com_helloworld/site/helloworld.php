<?php
defined('_JEXEC') or die('Restricted access');
/*
if (!JFactory::getUser()->authorise('core.manage', 'com_helloworld'))
{
    return JFactory::getApplication()->enqueueMessage(JText::_('JERROR_ALERTNOAUTHOR'), 'error');
}
*/

$controller = JControllerLegacy::getInstance('HelloWorld');
$input = JFactory::getApplication()->input;
$controller->execute($input->getCmd('task'));
$controller->redirect();
