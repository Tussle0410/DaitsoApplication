<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
    $sql="select * from user order by userPoint DESC limit 3";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
           $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('userID'=>$row["userID"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("user"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    else{
	echo "fail";
}
?>