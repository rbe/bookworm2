<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

namespace restbridge;

interface Command
{


    /**
     * Execute the command.
     *
     * @return object Result rows with values.
     *
     * @since 1.0
     */
    public function execute(): array;


}
