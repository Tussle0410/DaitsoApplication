<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$title=isset($_POST['title']) ? $_POST['title'] : '';
    $sql="select * from $title";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
           $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('storecomment'=>$row["storecomment"],
                'storeRating'=>$row["storeRating"],
                'userID'=>$row["userID"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("comment"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    else{
	echo "fail";
}
?>