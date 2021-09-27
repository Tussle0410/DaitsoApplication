<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
    $sql="select saveID from saveid where savecheck=1";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
           $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('saveID'=>$row["saveID"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("saveid"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    if($stmt->rowCount()==0){
	echo "no";
}
?>