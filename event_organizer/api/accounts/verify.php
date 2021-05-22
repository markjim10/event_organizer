<?php

include_once('../../db.php');
$username = $_POST['username'];
$code = $_POST['code'];

$sql = "SELECT * FROM user_account WHERE username='$username'";
$result = $conn->query($sql);

$user = $result->fetch_object();

if ($user->code == $code) {
    $sql = "UPDATE user_account SET status = 'verified' WHERE username = '$username'";
    $result = $conn->query($sql);
    echo "ok";
} else {
    echo "Wrong verification code";
}
