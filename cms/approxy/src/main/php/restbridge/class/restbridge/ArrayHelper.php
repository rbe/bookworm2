<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class ArrayHelper
{


    /**
     * Combine $k=>$v into '$k: $v'.
     *
     * @param array $a Array.
     *
     * @return array
     *
     * @since 1.0
     */
    public static function arrayCombineKeyValue(array $a): array
    {
        $b = [];
        foreach ($a as $k => $v) {
            $b[] = $k . ': ' . $v;
        }

        return $b;

    }//end arrayCombineKeyValue()


}//end class
