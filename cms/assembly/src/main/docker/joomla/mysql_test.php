<?php
$conn = mysqli_connect("mysql", "joomla", "joomla", "joomla");
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}
echo "Connected";
