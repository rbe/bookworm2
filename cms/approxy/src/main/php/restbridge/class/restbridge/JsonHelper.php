<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class JsonHelper
{

    /**
     * Comment.
     *
     * @var array
     *
     * @since 1.0
     */
    private array $json;


    /**
     * JsonHelper constructor.
     *
     * @param array $jsonAsArray JSON as array.
     *
     * @since 1.0
     */
    public function __construct(array $jsonAsArray)
    {
        $this->json = $jsonAsArray;
    }//end __construct()


    /**
     * Description.
     *
     * @param array $array
     * @return array|array[]
     *
     * @since 1.0
     */
    public function rowsWithValues(): array
    {
        if ($this->isSingleRow() === true) {
            return [$this->json];
        }

        return $this->json;

    }//end rowsWithValues()


    /**
     * Description.
     *
     * @param array $array Comment.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function isSingleRow(): bool
    {
        // if any value of this array does NOT contain an array
        $nestedArrayFound = false;
        foreach ($this->json as $value) {
            if (is_array($value)) {
                $nestedArrayFound = true;
            }
        }
        return !$nestedArrayFound;

    }//end isSingleRow()


    /**
     * Determine if JSON as array contains a single or multiple rows.
     *
     * @return int Number of rows in this JSON.
     *
     * @since 1.0
     */
    public function numberOfRows(): int
    {
        $num = 0;
        // Ask if every key is an array/a row
        foreach ($this->json as $key) {
            if (is_array($key)) {
                $num++;
            }
        }
        // Keys were data so we have just 1 row
        if ($num === 0 && is_array($this->json)) {
            $num++;
        }

        return $num;

    }//end numberOfRows()


}//end class
