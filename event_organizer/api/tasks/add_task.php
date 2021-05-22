<?php

include_once('../../db.php');

$task_id = $_POST['task_id'];
$event_id = $_POST['event_id'];

$sql = "UPDATE tasks SET event_id='$event_id' WHERE id='$task_id'";
if ($conn->query($sql) === TRUE) {
    echo 'success';
} else {
    echo json_encode($conn->error);
}
