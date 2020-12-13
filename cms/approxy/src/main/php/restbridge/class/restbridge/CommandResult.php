<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

namespace restbridge;

use Proxy\Exception\InvalidArgumentException;

class CommandResult
{

    /**
     * Description.
     *
     * @var int
     *
     * @since 1.0
     */
    private int $status;

    /**
     * Description.
     *
     * @var array
     *
     * @since 1.0
     */
    private array $meta;

    /**
     * Description.
     *
     * @var array
     *
     * @since 1.0
     */
    private array $data;


    /**
     * CommandResult constructor.
     *
     * @param int $status Comment.
     * @param array $meta
     * @param array $data Comment.
     *
     * @since 1.0
     */
    public function __construct(int $status, array $meta, array $data)
    {
        if (isset($status) === false || $status < 200)
        {
            throw new InvalidArgumentException($status);
        }
        $this->status = $status;
        $this->meta = $meta;
        $this->data = $data;
    }//end __construct()


    /**
     * Description.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function isOk(): bool
    {
        return $this->status >= 100 && $this->status < 400;
    }//end isOk()


    /**
     * Description.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function isError(): bool
    {
        return $this->status >= 400;
    }//end isError()


    /**
     * Description.
     *
     * @return array
     *
     * @since 1.0
     */
    public function getStatus(): array
    {
        return $this->status;
    }//end getStatus()


    /**
     * Description.
     *
     * @return array
     *
     * @since 1.0
     */
    public function getMeta(): array
    {
        return $this->meta;
    }//end getMeta()


    /**
     * Description.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function isDataEmpty(): bool
    {
        return empty($this->data) === true;
    }//end isDataEmpty()


    /**
     * Description.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function isDataNotEmpty(): bool
    {
        return empty($this->data) === false;
    }//end isDataNotEmpty()


    /**
     * Description.
     *
     * @return array
     *
     * @since 1.0
     */
    public function getData(): array
    {
        return $this->data;
    }//end getData()


    /**
     * Description.
     *
     * @param array $data Comment.
     *
     * @return void
     *
     * @since 1.0
     */
    public function updateData(array $data): void
    {
        $this->data = $data;
    }//end getData()


}//end class
