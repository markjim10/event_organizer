<?php

include_once('../../db.php');

$user_id = $_POST['user_id'];

$sql = "SELECT * FROM tasks WHERE event_id = '0' AND user_id='$user_id'";
$result = $conn->query($sql);

$tasks = array();

while ($row = mysqli_fetch_assoc($result)) {

    $task_id = $row['id'];
    $task_name = $row['task_name'];
    $task_expenses = $row['task_expenses'];
    $task_status = $row['task_status'];
    $event_id = $row['event_id'];
    $task_payment = $row['task_payment'];

    array_push($tasks, array(
        'task_id' => $task_id,
        'task_name' => $task_name,
        'task_expenses' => $task_expenses,
        'task_status' => $task_status,
        'event_id' => $event_id,
        'task_payment' => $task_payment
    ));
}

echo json_encode($tasks);
