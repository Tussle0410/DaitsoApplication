<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$storeno=isset($_POST['storeno']) ? $_POST['storeno'] : '';
    $sql="select storeRating from $storeno";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
        $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('storeRating'=>$row["storeRating"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("rating"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    if($stmt->rowCount()==0){
	echo "fail";
}
?>