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
use restbridge\JoomlaCmsAdapterImpl;
use restbridge\JsonHelper;

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
     * @since 1.0
     */
    private function executeCommand(array $matches): string
    {
        $commandName = trim($matches[1]);
        $parameters = trim($matches[2]);
        /** @var $restBridgePlugin RestBridgePlugin */
        $restBridgePlugin = $GLOBALS['restBridge']['PLUGIN'];
        if (isset($restBridgePlugin) === true) {
            $restBridgePlugin->customizeParameters($commandName, $parameters);
        }

        $commandResult = $this->commandExecutor->executeCommand($commandName, $parameters);
        if (is_array($commandResult) === true && array_key_exists('error', $commandResult)) {
            return $commandResult['error'];
        } else {
            $content = '';
            $hasMergableResult = is_array($commandResult) === true && empty($commandResult) === false;
            if ($hasMergableResult) {
                $jsonHelper = new JsonHelper($commandResult);
                $content = $this->cmsAdapter->renderTemplate($commandName, $jsonHelper->rowsWithValues());
            }
        }

        return $content;

    }//end executeCommand()


}//end class
