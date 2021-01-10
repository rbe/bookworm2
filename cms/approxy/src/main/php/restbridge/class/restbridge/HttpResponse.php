<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

namespace restbridge;

use JsonException;

final class HttpResponse
{

    /***
     * @var string
     *
     * @since 1.0
     */
    private string $id;

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
     * @param string $id Comment.
     * @param int $statusCode Comment.
     * @param string|null $body Comment.
     *
     * @since 1.0
     */
    public function __construct(string $id, int $statusCode, string $body = null)
    {
        $this->id = $id;
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
     * @return string
     *
     * @since 1.0
     */
    public function getBody(): string
    {
        return $this->body;

    }//end wasSuccessful()


    /**
     * Description.
     *
     * @return array
     *
     * @throws JsonException
     *
     * @since 1.0
     */
    public function getJson(): array
    {
        if ($this->wasSuccessful() === true && isset($this->json) === false) {
            $jsonResponse = json_decode($this->body, true, 512, JSON_THROW_ON_ERROR);
            if ($jsonResponse === false) {
                //showJsonError();
                restBridgeErrorLog('getJson: ' . $this->id . ' Error decoding response body: ' . json_last_error_msg());
                $this->json = json_decode('[false]');
            } else {
                if (is_bool($jsonResponse) === true) {
                    $this->json = ['result' => $jsonResponse];
                } else {
                    $this->json = $jsonResponse;
                }
            }
        } else {
            if ($this->statusCode >= 500) {
                restBridgeErrorLog('getJson: ' . $this->id . ' HTTP status ' . $this->statusCode . ', no JSON in response');
            }
            /* TODO [false] in Ordnung? */$this->json = json_decode('[false]');
        }

        return $this->json;

    }//end getBody()


    /**
     * Description.
     *
     * @return bool
     *
     * @since 1.0
     */
    public function wasSuccessful(): bool
    {
        return $this->statusCode >= 100 && $this->statusCode < 400;

    }//end getJson()


}//end class
