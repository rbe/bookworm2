<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

defined('_JEXEC') || die('Restricted access');

use restbridge\CmsAdapter;
use restbridge\CommandExecutor;
use restbridge\CommandResult;
use restbridge\Debugging;
use restbridge\Environment;
use restbridge\JoomlaCmsAdapterImpl;
use restbridge\JsonHelper;

require_once __DIR__ . '/restbridgeCommons.php';
require_once __DIR__ . '/restbridge_configuration.php';

// Check environment.
$environment = new Environment();
$environment->checkPhpVersion();

function isRestBridgeDebugLog()
{
    global $restBridge;
    return $restBridge['DEBUG'];
}//end isRestBridgeDebugLog()

function restBridgeInfoLog($msg)
{
    error_log('INFO: ' . $msg, 0);
}//end restBridgeInfoLog()

function restBridgeWarningLog($msg)
{
    error_log('WARNING: ' . $msg, 0);
}//end restBridgeWarningLog()

function restBridgeErrorLog($msg)
{
    error_log('ERROR: ' . $msg, 0);
}//end restBridgeErrorLog()

function restBridgeDebugLog($msg)
{
    if (isRestBridgeDebugLog()) {
        error_log('DEBUG: ' . $msg, 0);
    }
}//end restBridgeDebugLog()

// Enable debugging?
if (isRestBridgeDebugLog() === true) {
    Debugging::enable();
}

/**
 * Class plgContentRestbridge.
 *
 * @since 1.0
 */
final class plgContentRestbridge extends JPlugin
{

    /**
     * Description.
     *
     * @var CommandExecutor Comment.
     *
     * @since 1.0
     */
    private CommandExecutor $commandExecutor;

    /**
     * Description.
     *
     * @var CmsAdapter|JoomlaCmsAdapterImpl Comment.
     *
     * @since 1.0
     */
    private CmsAdapter $cmsAdapter;

    /**
     * Description.
     *
     * @var mixed|WbhRestBridgePlugin
     *
     * @since 1.0
     */
    private WbhRestBridgePlugin $restBridgePlugin;


    /**
     * Constructor.
     *
     * @param $subject The subject.
     * @param $params  The params.
     *
     * @since 1.0
     */
    public function __construct(&$subject, $params)
    {
        parent::__construct($subject, $params);
        $this->cmsAdapter = new JoomlaCmsAdapterImpl();
        $this->commandExecutor = new CommandExecutor($this->cmsAdapter);
        global $restBridge;
        $this->restBridgePlugin = $restBridge['PLUGIN'];

    }//end __construct()


    /**
     * Joomla! Event onContentPrepare.
     *
     * @param         $context The context.
     * @param         $article The article.
     * @param         $params  The params.
     * @param integer $page The page.
     *
     * @return boolean True or false.
     *
     * @since 1.0
     */
    public function onContentPrepare($context, &$article, &$params, $page = 0): bool
    {
        $regex = '/' .                 // delimiter
            '\\{' .                    // opening {
            '[\\s]*' .                 // skip whitespace
            'RestBridge' .             // required identifier
            '[\\s]*' .                 // skip whitespace
            ':' .                      // colon
            '[\\s]*' .                 // skip whitespace
            '([A-Za-z0-9_\\-]+)' .     // required command name
            '[\\s]*' .                 // skip whitespace
            '([A-Za-z0-9:,\s]{3,})*' . // parameter1:value1,parameter2:value2,...
            '[\\s]*' .                 // skip whitespace
            '\\}' .                    // closing }
            '/s';                      // delimiter
        $article->text = preg_replace_callback($regex, [$this, 'executeCommand'], $article->text);
        return true;

    }//end onContentPrepare()


    /**
     * Execute a command.
     *
     * @param array $matches Command name and parameters.
     *
     * @return string Content.
     *
     * @throws Exception
     * @since 1.0
     */
    private function executeCommand(array $matches): string
    {
        if (count($matches) !== 2) {
            restBridgeErrorLog('Cannot execute command, matches=' . print_r($matches, true));
            return '';
        }

        $commandName = trim($matches[1]);
        $urlParameters = trim($matches[2]);
        restBridgeDebugLog('Executing command ' . $commandName . ' with parameters ' . $urlParameters);
        if (isset($this->restBridgePlugin) === true) {
            $this->restBridgePlugin->customizeParameters($commandName, $urlParameters);
        }

        $commandResult = $this->commandExecutor->executeCommand($commandName, $urlParameters);
        restBridgeDebugLog('$commandResult=' . print_r($commandResult, true));
        if ($commandResult->isOk()) {
            if ($commandResult->isDataNotEmpty()) {
                $content = $this->renderData($commandName, $commandResult);
            } else {
                $content = $this->renderEmptyResult($commandName);
            }
        } else {
            $content = $this->renderError($commandName, $commandResult);
        }

        if (isset($this->restBridgePlugin) === true) {
            $content .= $this->restBridgePlugin->afterContentPrepared();
        }

        return $content;

    }//end executeCommand()


    /**
     * Description.
     *
     * @param string $commandName Comment.
     * @param CommandResult $commandResult Comment.
     *
     * @return string Comment.
     *
     * @since 1.0
     */
    private function renderData(string $commandName, CommandResult $commandResult): string
    {
        $jsonHelper = new JsonHelper($commandResult->getData());
        $content = $this->cmsAdapter->getModuleContent($commandName . '_Header');
        $content .= $this->cmsAdapter->renderTemplate($commandName . '_Content',
            $commandResult->getMeta(), $jsonHelper->rowsWithValues());
        $content .= $this->cmsAdapter->getModuleContent($commandName . '_Footer');
        return $content;
    }


    /**
     * Description.
     *
     * @param string $commandName Comment.
     *
     * @return string Comment.
     *
     * @since 1.0
     */
    private function renderEmptyResult(string $commandName): string
    {
        $content = $this->cmsAdapter->getModuleContent($commandName . '_EmptyResult_Header');
        $content .= $this->cmsAdapter->getModuleContent($commandName . '_EmptyResult_Content');
        $content .= $this->cmsAdapter->getModuleContent($commandName . '_EmptyResult_Footer');
        return $content;
    }


    /**
     * Description.
     *
     * @param string $commandName Comment.
     * @param CommandResult $commandResult Comment.
     *
     * @return string Comment.
     *
     * @since 1.0
     */
    private function renderError(string $commandName, CommandResult $commandResult): string
    {
        $jsonHelper = new JsonHelper($commandResult->getData()); // TODO getError
        $content = $this->cmsAdapter->getModuleContent($commandName . '_Error_Header');
        $content .= $this->cmsAdapter->renderTemplate($commandName . '_Error_Content',
            $commandResult->getMeta(), $jsonHelper->rowsWithValues());
        $content .= $this->cmsAdapter->getModuleContent($commandName . '_Error_Footer');
        return $content;
    }


}//end class
