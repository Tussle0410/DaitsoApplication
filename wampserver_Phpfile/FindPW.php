<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$userID=isset($_POST['userID']) ? $_POST['userID'] : '';
$userBirth=isset($_POST['userBirth']) ? $_POST['userBirth'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if ($userID != "" && $userBirth !="" ){ 
    $sql="select userPW from user where userID=BINARY('$userID') and userBirth='$userBirth'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
   
    if ($stmt->rowCount() > 0)
    {
   $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('userPW'=>$row["userPW"]
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
         ID: <input type = "text" name = "userID" />
         생년월일: <input type = "text" name = "userBirth" />
         <input type = "submit" />
      </form>
   </body>
</html>
<?php
}
?>