<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

abstract class AbstractRestBridgePlugin
{

    /**
     * @var CmsAdapter Comment.
     *
     * @since 1.0
     */
    protected CmsAdapter $cmsAdapter;


    /**
     * AbstractRestBridgePlugin constructor.
     *
     * @param CmsAdapter $cmsAdapter The CMS adapter.
     *
     * @since 1.0
     */
    protected function __construct(CmsAdapter $cmsAdapter)
    {
        $this->cmsAdapter = $cmsAdapter;

    }//end __construct()


    /**
     * Description.
     *
     * @param string $commandName Comment.
     * @param string $parameters Comment.
     *
     * @return mixed Comment.
     *
     * @since 1.0
     */
    abstract public function customizeParameters(string $commandName, string &$parameters);


    abstract public function beforeCommandExecution(string $commandName, string &$urlParameters): string;


    abstract public function afterCommandExecution(string $commandName, string $urlParameters, string &$content): string;


}//end class
