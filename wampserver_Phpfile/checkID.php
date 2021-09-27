<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$userID=isset($_POST['userID']) ? $_POST['userID'] : '';
if ($userID != "" ){ 
    $sql="select * from user where userID=BINARY('$userID')";
    $stmt = $con->prepare($sql);
    $stmt->execute();
    if ($stmt->rowCount() > 0){
        echo "no";
    }
    if($stmt->rowCount()==0){
	echo "yes";
}
}
else {
    echo "검색할 ID를 입력하세요 ";
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
         <input type = "submit" />
      </form>
   </body>
</html>
<?php
}
?>