<?php

include_once('../../db.php');

$user_id = $_POST['user_id'];
$sql = "SELECT events.event_name, 
    SUM(tasks.task_expenses) as total
FROM events
JOIN tasks on tasks.event_id = events.id
WHERE events.user_id ='$user_id'
GROUP BY events.id ";

if ($result = $conn->query($sql)) {
} else {
    echo json_encode($conn->error);
}

$expenses = array();

while ($row = mysqli_fetch_assoc($result)) {

    $event_name = $row['event_name'];
    $total = $row['total'];

    array_push($expenses, array(
        'event_name' => $event_name,
        'total' => $total
    ));
}
echo json_encode($expenses);
