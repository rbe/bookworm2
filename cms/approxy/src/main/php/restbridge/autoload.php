<?php

require_once __DIR__ . '/vendor/autoload.php';

define('MY_CLASS_DIR', __DIR__ . '/class/');
$include_path = get_include_path() . PATH_SEPARATOR . MY_CLASS_DIR;
set_include_path($include_path);

spl_autoload_register(function ($class) {
    $path = str_replace("\\", "/", $class);
    include($path . '.php');
});
