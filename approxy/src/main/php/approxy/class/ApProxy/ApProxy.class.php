<?php

namespace ApProxy;

use Proxy\Factory;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ApProxy
{

    /**
     * @var AppInfo
     */
    protected $appInfo;

    /**
     * @var string
     */
    protected $requestUri;

    /**
     * @param $appInfo AppInfo
     * @param $requestUri string
     */
    public function __construct($appInfo, $requestUri)
    {
        $this->appInfo = $appInfo;
        $this->requestUri = $requestUri;
    }

    /**
     * @param $appRequestUri string
     */
    private function requestResponseWithApp($appRequestUri)
    {
        $proxy_destination = $this->appInfo->getProxyDestination();
        $parameterSet = isset($proxy_destination) && isset($appRequestUri);
        if ($parameterSet) {
            $forwardToUrl = $proxy_destination . $appRequestUri;
            $request = Request::createFromGlobals();
            $request->headers->set('Connection', 'close');
            $response = Factory::forward($request)->to($forwardToUrl);
            // Do not use $response->send(); to fix curl, Safari + Transfer-Encoding:
            // The proxy does not send size for each chunk
            $response->headers->remove('Transfer-Encoding');
            $content = $response->getContent();
            $response->headers->set('Content-Length', strlen($content));
            $response->sendHeaders();
            echo $content;
        } else {
            HttpHelper::sendHttpRedirectWithStatus('NO_APP_REQUEST');
        }
    }

    /**
     * @param $customizeUriDelegate
     */
    public function perform($customizeUriDelegate)
    {
        if (isset($customizeUriDelegate)) {
            $appUri = $customizeUriDelegate($this->appInfo, $this->requestUri);
        }
        if (isset($appUri)) {
            $this->requestResponseWithApp($appUri);
        } else if (isset($requestUri)) {
            $this->requestResponseWithApp($requestUri);
        } else {
            HttpHelper::sendHttpRedirectWithStatus('NO_APP_URI');
        }
    }

}

?>
