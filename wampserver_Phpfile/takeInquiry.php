<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$userID=isset($_POST['userID']) ? $_POST['userID'] : '';
    $sql="select * from inquiry where userID='$userID'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
           $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('inquiryTitle'=>$row["inquiryTitle"],
                'inquiryComment'=>$row["inquiryComment"],
                'inquiryNo'=>$row["inquiryNo"],
                'answerCheck'=>$row["answerCheck"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("inquiry"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    else{
	echo "fail";
}
?>