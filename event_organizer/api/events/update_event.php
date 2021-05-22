<?php

include_once('../../db.php');

$eventName = $_POST['eventName'];
$eventDate = $_POST['eventDate'];
$eventId = $_POST['eventId'];

if (strtotime($eventDate) < time()) {
    echo "The date you entered has passed";
} else {

    $sql = "UPDATE events SET    event_name='$eventName', 
                            event_date='$eventDate'
                            WHERE id='$eventId'";
    if ($conn->query($sql)) {
        echo "success";
    } else {
        echo json_encode($conn->error);
    }
}

// $event_id = $_POST['event_id'];
// $task_id = $_POST['task_id'];
// $task_name = $_POST['task_name'];
// $task_expenses = $_POST['task_expenses'];
// $task_payment = $_POST['task_payment'];
// $task_status = strtolower($_POST['task_status']);


// if ($conn->query($sql) === TRUE) {

//     $sql = "SELECT * FROM tasks WHERE event_id='$event_id' AND task_status='pending'";

//     $result = $conn->query($sql);

//     if ($result->num_rows > 0) {
//         $sql = "UPDATE events SET event_status='in-progress' WHERE id='$event_id'";
//         echo "in progress";
//     } else {
//         $sql = "UPDATE events SET event_status='completed' WHERE id='$event_id'";
//         echo "completed";
//     }
//     $result = $conn->query($sql);
// } else {
//     echo json_encode($conn->error);
// }
