<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

defined('_JEXEC') || die('Restricted access');

require_once __DIR__ . '/autoload.php';

/**
 * Class plgContentRestbridge.
 */
class plgContentRestbridge extends JPlugin
{

    /**
     * Constructor.
     *
     * @param $subject The subject.
     * @param $params The params.
     */
    public function __construct(&$subject, $params)
    {
        parent::__construct($subject, $params);
    }//end __construct()

    /**
     * Joomla! Event onContentPrepare.
     *
     * @param $context The context.
     * @param $article The article.
     * @param $params The params.
     * @param int $page The page.
     *
     * @return bool True or false.
     */
    public function onContentPrepare($context, &$article, &$params, $page = 0): bool
    {
        $regex =
            '/' .                      // delimiter
            '\\{' .                    // opening {
            '[\\s]*' .                 // skip whitespace
            'Bookworm' .               // required identifier
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
     */
    private function executeCommand(array $matches): string
    {
        $commands = new Commands();
        return $commands->executeCommand($matches);
    }//end executeCommand()

    /**
     * Get a value for a field from comprofiler's database for an user.
     * @param $user
     * @param $field
     * @return string
     */
    private function getUserValueFromComprofiler(string $user, string $field): string
    {
        $db = JFactory::getDBO();
        $query = 'SELECT ' . $field . ' FROM #__comprofiler WHERE user_id=' . $user->id;
        $db->setQuery($query);
        return $db->loadResult();
    }

    /**
     * Get user's data.
     * @return string|null
     * @throws Exception
     */
    private function getHoerernummer(): string
    {
        $user = JFactory::getUser();
        if (isset($user)) {
            $hnr = getUserValueFromComprofiler($user, 'cb_hoerernummer');
            if (isset($hnr)) {
                return $hnr;
            }
        }
        throw new Exception();
    }

}//end class
