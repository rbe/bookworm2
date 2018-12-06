<?php

namespace ApProxy;

class ApProxyFactory
{

    /**
     * @var array AppInfo[]
     */
    private static $appInfos = array();

    private function __construct()
    {
    }

    /**
     * @param $requestUri string
     * @return AppInfo|null
     */
    private static function findApp($requestUri)
    {
        $parsedRequestUri = parse_url($requestUri);
        foreach (static::$appInfos as $appInfo) {
            $appContextPath = $appInfo->getContextPath();
            $requestUriContextPath = substr($parsedRequestUri['path'], 0, strlen($appContextPath));
            $uriBeginsWithAppContext = $requestUriContextPath == $appContextPath;
            if ($uriBeginsWithAppContext) {
                $foundAppInfo = $appInfo;
                break;
            }
        }
        return isset($foundAppInfo) ? $foundAppInfo : null;
    }

    /**
     * Configure factory: add an AppInfo for creating proxies.
     * @param $appInfo AppInfo
     */
    public static function configure($appInfo)
    {
        array_push(static::$appInfos, $appInfo);
    }

    /**
     * @param $requestUri string
     * @return ApProxy|null
     */
    public static function create($requestUri)
    {
        $app = static::findApp($requestUri);
        if (isset($app)) {
            return new ApProxy($app, $requestUri);
        } else {
            return null;
        }
    }

}
