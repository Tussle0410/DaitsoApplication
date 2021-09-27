<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$userName=isset($_POST['userName']) ? $_POST['userName'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if ($userName != "" ){ 
    $sql="select userID from user where userName='$userName'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
   
    if ($stmt->rowCount() > 0)
    {
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
    if($stmt->rowCount()==0){
	echo "no";
}
}

?>
<?php
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if (!$android){
?>
<html>
   <body>
      <form action="<?php $_PHP_SELF ?>" method="POST">
         이름: <input type = "text" name = "userName" />
         <input type = "submit" />
      </form>
   </body>
</html>
<?php
}
?>