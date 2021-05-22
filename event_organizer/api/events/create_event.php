<?php

include_once('../../db.php');

$eventName = $_POST['eventName'];
$eventDate = $_POST['eventDate'];
$user_id = $_POST['user_id'];

if (strtotime($eventDate) < time()) {
    echo "error";
} else {
    $sql = "INSERT INTO events VALUES('','$eventName', '$eventDate', 'in-progress',  '$user_id')";

    if ($conn->query($sql) === TRUE) {
        echo 'success';
    } else {
        echo json_encode($conn->error);
    }
}
