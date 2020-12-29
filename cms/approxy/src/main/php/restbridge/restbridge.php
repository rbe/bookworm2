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
use restbridge\JoomlaCmsAdapterImpl;
use restbridge\JsonHelper;

require_once __DIR__ . '/restbridgeCommons.php';
require_once __DIR__ . '/restbridge_configuration.php';

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
        if (count($matches) < 2) {
            restBridgeErrorLog('executeCommand: Cannot execute, matches=' . print_r($matches, true));
            return '';
        }

        $commandName = trim($matches[1]);
        $urlParameters = trim($matches[2]);
        restBridgeDebugLog('executeCommand: ' . $commandName . ' with parameters ' . $urlParameters);

        $content = '';
        if (isset($this->restBridgePlugin) === true) {
            $content .= $this->restBridgePlugin->beforeCommandExecution($commandName, $urlParameters);
        }

        if (isset($this->restBridgePlugin) === true) {
            $this->restBridgePlugin->customizeParameters($commandName, $urlParameters);
        }

        $commandResult = $this->commandExecutor->executeCommand($commandName, $urlParameters);
        if ($commandResult->isOk()) {
            if ($commandResult->isDataNotEmpty()) {
                $content = $this->renderData($commandName, $commandResult);
            } else {
                $content = $this->renderEmptyResult($commandName);
            }
        } else if ($commandResult->isNotFound()) {
            $content = $this->renderEmptyResult($commandName);
        } else if ($commandResult->isError()) {
            $content = $this->renderError($commandName, $commandResult);
        } else {
            restBridgeWarningLog('Unhandled result of command execution');
        }

        if (isset($this->restBridgePlugin) === true) {
            $content .= $this->restBridgePlugin->afterCommandExecution($commandName, $urlParameters, $content);
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
        $content = $this->cmsAdapter->renderTemplate($commandName . '_Header',
            $commandResult->getMeta(), []);
        $content .= $this->cmsAdapter->renderTemplate($commandName . '_Content',
            $commandResult->getMeta(), $jsonHelper->rowsWithValues());
        $content .= $this->cmsAdapter->renderTemplate($commandName . '_Footer',
            $commandResult->getMeta(), []);
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
        $jsonHelper = new JsonHelper($commandResult->getData());
        $content = $this->cmsAdapter->renderTemplate($commandName . '_Error_Header',
            $commandResult->getMeta(), []);
        $content .= $this->cmsAdapter->renderTemplate($commandName . '_Error_Content',
            $commandResult->getMeta(), $jsonHelper->rowsWithValues(),
            'Custom module ' . $commandName . '_Error_Content' . ' not defined');
        $content .= $this->cmsAdapter->renderTemplate($commandName . '_Error_Footer',
            $commandResult->getMeta(), []);
        return $content;
    }


}//end class
