<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    $sql="update saveid set savecheck=0";
    $stmt = $con->prepare($sql);
    $stmt->execute();

?>
