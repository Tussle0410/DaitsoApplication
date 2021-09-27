<?php 
    error_reporting(E_ALL); 
    ini_set('display_errors',1); 
    include('dbcon.php');
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
        $userID=$_POST['userID'];
        $userPW=$_POST['userPW'];
        $userName=$_POST['userName'];
        $userBirth=$_POST['userBirth'];
        $userSex=$_POST['userSex'];
                    if(empty($userID)){
            		$errMSG = "이름을 입력하세요.";
        		}
        	     else if(empty($userPW)){
            		$errMSG = "비밀번호를 입력하세요.";
       		 }
        	   else if(empty($userName)){
            		$errMSG = "이름을 입력하세요.";
       		 }
        else if(empty($userBirth)){
            $errMSG = "생년월일을 입력하세요.";
        }
        else if(empty($userSex)){
            $errMSG = "성별을 확인해주세요.";
        }

        if(!isset($errMSG)) 
        {
            try{
                $stmt = $con->prepare('INSERT INTO user(userID, userPW, userName, userBirth, userSex) VALUES(:userID, :userPW, :userName, :userBirth, :userSex)');
                $stmt->bindParam(':userID', $userID);
                $stmt->bindParam(':userPW', $userPW);
                $stmt->bindParam(':userName', $userName);
                $stmt->bindParam(':userBirth', $userBirth);
                $stmt->bindParam(':userSex', $userSex);
                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }
            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }
    }
?>
