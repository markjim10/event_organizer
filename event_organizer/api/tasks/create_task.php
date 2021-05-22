<?php

include_once('../../db.php');

$user_id = $_POST['user_id'];
$task_name = $_POST['task_name'];
$task_expenses = $_POST['task_expense'];

$sql = "INSERT INTO tasks VALUES('','$task_name', '$task_expenses', 'pending', 'pending',  '0', '$user_id')";

if ($conn->query($sql) === TRUE) {
    echo 'success';
} else {
    echo json_encode($conn->error);
}
