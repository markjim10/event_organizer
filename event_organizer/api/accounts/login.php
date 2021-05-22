<?php

include_once('../../db.php');

$username = $_POST['username'];
$password = $_POST['password'];

$sql = "SELECT * FROM user_account WHERE username= '$username' AND password = '$password'";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $user = $result->fetch_object();

    if ($user->status == "verified") {
        echo json_encode($user);
    } else {
        echo "User not verified";
    }
} else {
    echo 'Invalid credentials';
}
