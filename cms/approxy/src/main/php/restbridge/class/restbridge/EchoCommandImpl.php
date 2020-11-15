<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class EchoCommandImpl extends AbstractCommand
{


    /**
     * Echo input parameters in two rows.
     *
     * @return object
     *
     * @since 1.0
     */
    public function execute(): object
    {
        return [
            [
                'titelnummer' => '1__' . $this->parameters['titelnummer'] . '__081542',
                'name' => '1__Christian',
            ],
            [
                'titelnummer' => '2__' . $this->parameters['titelnummer'] . '__081542',
                'name' => '2__' . $this->parameters['name'],
            ],
        ];

    }//end execute()


}//end class
