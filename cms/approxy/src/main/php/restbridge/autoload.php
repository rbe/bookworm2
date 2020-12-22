<?php

require_once __DIR__ . '/vendor/autoload.php';

define('MY_ROOT_DIR', __DIR__ . '/../../..');
define('MY_LIBRARIES_DIR', MY_ROOT_DIR . '/libraries/');
define('MY_CLASS_DIR', __DIR__ . '/class/');
$include_path = get_include_path() . PATH_SEPARATOR . MY_CLASS_DIR
 . PATH_SEPARATOR . MY_LIBRARIES_DIR;
set_include_path($include_path);

/*
error_log('MY_ROOT_DIR: ' . MY_ROOT_DIR);
error_log('MY_LIBRARIES_DIR: ' . MY_LIBRARIES_DIR);
error_log('MY_CLASS_DIR: ' . MY_CLASS_DIR);
error_log('$include_path=' . $include_path);
*/

spl_autoload_register(function ($class) {
    $path = str_replace("\\", "/", $class);
    $filename = $path . '.php';
    $found = stream_resolve_include_path($filename);
    //error_log('$found=' . $found);
    if ($found !== false) {
        include($filename);
    }
});
