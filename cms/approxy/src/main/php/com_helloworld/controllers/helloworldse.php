<?php
defined('_JEXEC') or die;

class HelloWorldControllerHelloworldse extends JControllerAdmin
{
    public function getModel($name = 'Location', $prefix = 'LocationModel', $config = array('ignore_request' => true))
    {
        $model = parent::getModel($name, $prefix, $config);
        return $model;
    }
}
