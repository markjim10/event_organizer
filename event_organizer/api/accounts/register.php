<?php
include_once('../../db.php');
include_once('../../email_auth.php');

use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\PHPMailer;

require '../../vendor/phpmailer/phpmailer/src/Exception.php';
require '../../vendor/phpmailer/phpmailer/src/PHPMailer.php';
require '../../vendor/phpmailer/phpmailer/src/SMTP.php';

$email = $_POST['email'];
$uname = $_POST['username'];
$mobile = $_POST['mobile'];
$pword = $_POST['password'];
$emailstats = 'notverified';
$code = rand(999999, 111111);

$_SESSION['username'] = $uname;

// check if username already exists
$sql = "SELECT * FROM user_account WHERE username='$uname' OR email='$email'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    echo "account already exists";
} else {
    $mail = new PHPMailer(true);
    $mail->isSMTP();                                            //Send using SMTP
    $mail->Host       = 'smtp.gmail.com';                    //Set the SMTP server to send through
    $mail->SMTPAuth   = true;                                   //Enable SMTP authentication
    $mail->Username   = $auth_email;                     //SMTP username
    $mail->Password   = $auth_password;                               //SMTP password
    $mail->SMTPSecure = 'ssl';                                  //Enable TLS encryption; `PHPMailer::ENCRYPTION_SMTPS` encouraged
    $mail->Port       = 465;                                    //TCP port to connect to, use 465 for `PHPMailer::ENCRYPTION_SMTPS` above

    //Recipients
    $mail->setFrom('eventorganizer@gmail.com', 'Event Organizer');
    $mail->addAddress($email);
    $mail->addReplyTo('no-reply@eventorganizer.com', 'No Reply');
    $mail->isHTML(true);

    $sql = "INSERT INTO user_account(mobile, email, username, password, code, status) 
                            VALUES('$mobile', '$email', '$uname','$pword', '$code', '$emailstats')";

    if ($conn->query($sql) === TRUE) {

        $mail->Subject = 'Email Verification Code';
        $mail->Body    = "Hello $email Your verification code is $code";
        $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';

        if ($mail->send()) {
            echo "sent a verification code";
        } else {
            echo "failed sending code";
        }
    } else {
        echo "failed inserting to db";
    }
}
