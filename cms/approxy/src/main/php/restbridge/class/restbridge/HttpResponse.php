<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

declare(strict_types=1);

namespace restbridge;

final class HttpResponse
{

    private int $statusCode;
    private string $body;
    private object $json;

    /**
     * Response constructor.
     *
     * @param int $statusCode
     * @param string $body
     */
    public function __construct(int $statusCode, string $body)
    {
        $this->statusCode = $statusCode;
        $this->body = $body;
    }

    /**
     *
     * @return int
     *
     * @since version
     */
    public function getStatusCode(): int
    {
        return $this->statusCode;
    }

    /**
     *
     * @return bool
     *
     * @since version
     */
    public function wasSuccessful(): bool
    {
        $json = json_decode($this->body);
        if ($json === false) {
            error_log('Error decoding response body as JSON', 0);
        } else {
            $this->json = $json;
        }
        return $this->json
            && $this->statusCode >= 200 && $this->statusCode < 400;
    }

    /**
     *
     * @return string
     *
     * @since version
     */
    public function getBody(): string
    {
        return $this->body;
    }

    /**
     *
     * @return object
     *
     * @since version
     */
    public function getJson(): object
    {
        return $this->json;
    }

}//end class
