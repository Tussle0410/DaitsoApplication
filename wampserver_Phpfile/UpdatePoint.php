<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon1.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
$user=isset($_POST['user']) ? $_POST['user'] : '';
    $sql="update user set userPoint = userPoint + $user where userID='$user'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
	echo "ok";

?>