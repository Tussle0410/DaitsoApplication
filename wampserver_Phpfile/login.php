<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$userID=isset($_POST['userID']) ? $_POST['userID'] : '';
$userPW=isset($_POST['userPW']) ? $_POST['userPW'] : '';
if ($userID != "" && $userPW != "" ){ 
    $sql="select * from user where userID=BINARY('$userID') and userPW = BINARY('$userPW')";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
        $data = array(); 
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
        	extract($row);
            array_push($data, 
                array('userID'=>$row["userID"],
                'userPW'=>$row["userPW"],
                'userName'=>$row["userName"],
                'userBirth'=>$row["userBirth"],
                'userSex'=>$row["userSex"],
                'userNo'=>$row["userNo"],
                'userPoint'=>$row["userPoint"]
            ));
        }
header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("user"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    if($stmt->rowCount()==0){
	echo "fail";
}
}
else {
    echo "check";
}
?>
<?php
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if (!$android){
?>
<html>
   <body>
      <form action="<?php $_PHP_SELF ?>" method="POST">
         아이디: <input type = "text" name = "userID" />
         비밀번호: <input type="text" name="userPW"/>
         <input type = "submit" />
      </form>
   </body>
</html>
<?php
}
?>