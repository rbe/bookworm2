<?php
defined('_JEXEC') or die('Restricted access');

class ModHelloWorldHelper
{
    /**
     * Retrieves the hello message
     *
     * @param array $params An object containing the module parameters
     *
     * @access public
     */
    public static function getHello($params)
    {
        return 'Hello, World!';
    }
}
