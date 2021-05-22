<?php

include_once('../../db.php');

$user_id = $_POST['user_id'];
$email = $_POST['email'];
$mobile = $_POST['mobile'];

$sql = "UPDATE user_account SET email='$email', mobile='$mobile' WHERE id='$user_id'";

if ($conn->query($sql) === TRUE) {
    echo "success";
} else {
    echo "error";
}
