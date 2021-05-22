<?php

include_once('../../db.php');


$user_id = $_POST['user_id'];
$oldPass = $_POST['oldPass'];
$newPass = $_POST['newPass'];


$sql = "SELECT password FROM user_account WHERE id='$user_id'";
$result = $conn->query($sql);
$row = $result->fetch_object();

if ($oldPass != $row->password) {
    echo "Wrong password";
} else {
    $sql = "UPDATE user_account SET password='$newPass' WHERE id='$user_id'";
    $conn->query($sql);
    echo "success";
}



// $sql = "UPDATE user_account SET email='$email', mobile='$mobile' WHERE id='$user_id'";

// if ($conn->query($sql) === TRUE) {
//     echo "success";
// } else {
//     echo "error";
// }
