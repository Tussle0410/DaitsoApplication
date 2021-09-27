<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$region=isset($_POST['region']) ? $_POST['region'] : '';
    $sql="select * from store where region='$region' order by storeViews desc limit 5";
    $stmt = $con->prepare($sql);
    $stmt->execute();
   
    if ($stmt->rowCount() > 0)
    {
   $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('storeName'=>$row["storeName"],
                'storeAddress'=>$row["storeAddress"],
                'storeKinds'=>$row["storeKinds"],
                'storeRating'=>$row["storeRating"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("top_list"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
}
    if($stmt->rowCount()==0){
	echo "no";
}

?>
