<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

final class HttpResponse
{

    /**
     * @var int
     *
     * @since 1.0
     */
    private int $statusCode;

    /**
     * @var ?string
     *
     * @since 1.0
     */
    private ?string $body;

    /**
     * @var ?array
     *
     * @since 1.0
     */
    private ?array $json;


    /**
     * Response constructor.
     *
     * @param int $statusCode Comment.
     * @param string|null $body Comment.
     *
     * @since 1.0
     */
    public function __construct(int $statusCode, string $body = null)
    {
        $this->statusCode = $statusCode;
        $this->body = $body;
    }//end __construct()


    /**
     * Description.
     *
     * @return int
     *
     * @since 1.0
     */
    public function getStatusCode(): int
    {
        return $this->statusCode;
    }//end getStatusCode()


    /**
     * Description.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function wasSuccessful(): bool
    {
        return $this->statusCode >= 200 && $this->statusCode < 400;

    }//end wasSuccessful()


    /**
     * Description.
     *
     * @return string
     *
     * @since 1.0
     */
    public function getBody(): string
    {
        return $this->body;

    }//end getBody()


    /**
     * Description.
     *
     * @return array
     *
     * @throws \JsonException
     *
     * @since 1.0
     */
    public function getJson(): array
    {
        if ($this->wasSuccessful() === true && isset($this->json) === false) {
            $jsonResponse = json_decode($this->body, true, 512, JSON_THROW_ON_ERROR);
            if ($jsonResponse === false) {
                showJsonError();
                error_log('Error decoding response body as JSON: ' . json_last_error_msg(), 0);
                $this->json = json_decode('');
            } else {
                $this->json = $jsonResponse;
            }
        } else {
            error_log('No JSON response, request was not successful, HTTP status ' . $this->statusCode, 0);
        }

        return $this->json;

    }//end getJson()


}//end class
