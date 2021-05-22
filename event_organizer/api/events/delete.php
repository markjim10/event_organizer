<?php

include_once('../../db.php');

$event_id = $_POST['event_id'];

$sql = "DELETE FROM events WHERE id='$event_id'";
if ($conn->query($sql) === TRUE) {
    $sql = "UPDATE tasks SET event_id='0' WHERE event_id='$event_id'";
    if ($conn->query($sql) === TRUE) {
        echo 'success';
    } else {
        echo json_encode($conn->error);
    }
} else {
    echo json_encode($conn->error);
}
