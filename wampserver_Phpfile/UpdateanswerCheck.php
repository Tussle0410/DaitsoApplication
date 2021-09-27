<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$no = isset($_POST['no']) ? $_POST['no'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    $sql="update inquiry set answerCheck=1 where inquiryNo='$no'";
    $stmt = $con->prepare($sql);
    $stmt->execute();

?>
