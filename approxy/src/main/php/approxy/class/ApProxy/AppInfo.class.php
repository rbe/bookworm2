<?php

namespace ApProxy;

class AppInfo
{

    /**
     * @var string
     */
    private $app_name;

    /**
     * @var string
     */
    private $proxy_destination;

    /**
     * @var string
     */
    private $context_path;

    /**
     * AppInfo constructor.
     * @param $app_name
     * @param $proxy_destination
     * @param $context_path
     */
    public function __construct($app_name, $proxy_destination, $context_path)
    {
        $this->app_name = $app_name;
        $this->proxy_destination = $proxy_destination;
        $this->context_path = $context_path;
    }

    /**
     * @return string
     */
    public function getAppName()
    {
        return $this->app_name;
    }

    /**
     * @return string
     */
    public function getProxyDestination()
    {
        return $this->proxy_destination;
    }

    /**
     * @return string
     */
    public function getContextPath()
    {
        return $this->context_path;
    }

}
