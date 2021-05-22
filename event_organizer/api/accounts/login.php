<?php

include_once('../../db.php');

$uname = $_POST['username'];
$pword = $_POST['password'];

$sql = "SELECT * FROM user_account WHERE username= '$uname' AND password = '$pword'";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $user = $result->fetch_object();

    if ($user->emailstats == "verified") {
        echo json_encode($user);
    } else {
        echo "User not verified";
    }
} else {
    echo 'Invalid credentials';
}
