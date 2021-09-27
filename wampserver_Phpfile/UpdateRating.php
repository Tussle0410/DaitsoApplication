<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
$title=isset($_POST['title']) ? $_POST['title'] : '';
$rating=isset($_POST['rating']) ? $_POST['rating'] : '';
    $sql="update store set storeRating = $rating where storeName='$title'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
	echo "ok";

?>