<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$title=isset($_POST['title']) ? $_POST['title'] : '';
$sql = "update store set storeViews = storeViews + 1 where storeName='$title'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
echo "ok";
?>