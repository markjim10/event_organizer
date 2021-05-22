<?php

include_once('../../db.php');
date_default_timezone_set('Asia/Singapore');
$id = $_POST['id'];

$sql = "SELECT * FROM events WHERE user_id='$id' ORDER BY id DESC";
$result = $conn->query($sql);

$events = array();

while ($row = mysqli_fetch_assoc($result)) {
    $event_id = $row['id'];
    $event_name = $row['event_name'];
    $event_date = $row['event_date'];
    $event_status = $row['event_status'];

    $pending = mysqli_query($conn, "SELECT COUNT(*) 
                            FROM `tasks` 
                            WHERE NOT `task_status` = 'pending' 
                            AND `event_id` = '$event_id' ");

    $total = mysqli_query($conn, "SELECT COUNT(*) 
                FROM `tasks` WHERE `event_id` = '$event_id'");

    $pendingCount = mysqli_fetch_assoc($pending)['COUNT(*)'];
    $totalCount = mysqli_fetch_assoc($total)['COUNT(*)'];

    $hourAndMins = substr($event_date, 0, -6);

    $future = strtotime($event_date);
    $today = time();
    $timeleft = $future - $today;
    $daysleft = round((($timeleft / 24) / 60) / 60);
    $daysleft = strval($daysleft);



    if ($daysleft === "-0") {
        $daysleft = "Your event is today";
    } else if ($daysleft == "0" || $daysleft == "1") {
        $daysleft = "Your event is tomorrow";
    } else if ($daysleft < 0) {
        $daysleft = "Event has passed";
    } else {
        $daysleft = $daysleft . " days left";
    }


    array_push($events, array(
        'event_id' => $event_id,
        'event_name' => $event_name,
        'event_date' => $event_date,
        'event_status' => $event_status,
        'days_left' => $daysleft,
        'event_task_status' => $pendingCount . " of " . $totalCount . " tasks completed"
    ));
}


echo json_encode($events);
