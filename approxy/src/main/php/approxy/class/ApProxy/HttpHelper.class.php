<?php

namespace ApProxy;

class HttpHelper
{

    private function __construct()
    {
    }

    /**
     * @param $status
     */
    public static function sendHttpRedirectWithStatus($status)
    {
        http_response_code(302);
        header('Location: /?status=' . $status);
    }

}
