<?php

include_once('../../db.php');

$task_id = $_POST['task_id'];

$sql = "DELETE FROM tasks WHERE id='$task_id'";
if ($conn->query($sql) === TRUE) {
    echo 'success';
} else {
    echo json_encode($conn->error);
}
