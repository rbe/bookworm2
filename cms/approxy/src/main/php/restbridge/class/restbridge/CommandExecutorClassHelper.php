<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

/**
 * Helper for dynamically executing methods.
 *
 * @since 1.0
 */
final class CommandExecutorClassHelper
{


    /**
     * Dynamically call a method of an object.
     *
     * @param string $commandName Name of command.
     * @param string[][] $parameterArray Parameters (key/value).
     * @param object $instance Object instance to call method on.
     *
     * @return string[]
     * @since  version
     */
    public static function callMethod(string $commandName, array $parameterArray, object $instance): array
    {
        $methodName = 'cmd' . $commandName;
        return method_exists($instance, $methodName) === true
            ? $instance->{$methodName}($parameterArray)
            : ['message' => 'Method ' . $methodName . ' not found'];

    }//end callMethod()


}//end class
