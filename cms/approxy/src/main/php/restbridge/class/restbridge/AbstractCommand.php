<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

abstract class AbstractCommand implements Command
{

    /**
     * @var array
     *
     * @since 1.0
     */
    protected array $parameters;


    /**
     * EchoCommandImpl constructor.
     *
     * @param array $parameters The parameters.
     *
     * @since 1.0
     */
    public function __construct(array $parameters)
    {
        $this->parameters = $parameters;

    }//end __construct()


}//end class
