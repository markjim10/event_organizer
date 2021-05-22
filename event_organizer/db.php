<?php

$db_host = "localhost";
$db_username = "root";
$db_password = "";
$db_name = "eventorganizer";

$conn = mysqli_connect($db_host, $db_username, $db_password, $db_name);

if (!$conn) {
    die("Unable to select database");
}
