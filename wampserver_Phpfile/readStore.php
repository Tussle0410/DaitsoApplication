<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$title=isset($_POST['title']) ? $_POST['title'] : '';
$address=isset($_POST['address']) ? $_POST['address'] : '';
$kinds=isset($_POST['kinds']) ? $_POST['kinds'] : '';
    $sql="select * from store where storeName = '$title'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
           $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('storeName'=>$row["storeName"],
                'storeAddress'=>$row["storeAddress"],
                'storeViews'=>$row["storeViews"],
                'storeKinds'=>$row["storeKinds"],
                'storeRating'=>$row["storeRating"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("newStore"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;

?>