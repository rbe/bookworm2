<?php

require_once __DIR__ . '/vendor/autoload.php';

define('APPROXY_CLASS_DIR', __DIR__ . '/class/');
$include_path = get_include_path() . PATH_SEPARATOR . APPROXY_CLASS_DIR;
set_include_path($include_path);
spl_autoload_extensions('.class.php');
spl_autoload_register(function ($class) {
    $path = str_replace("\\", "/", $class);
    include($path . '.class.php');
});

?>
