<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$userID=isset($_POST['userID']) ? $_POST['userID'] : '';
    $sql="update saveid set saveID = '$userID', savecheck=1";
    $stmt = $con->prepare($sql);
    $stmt->execute();

?>