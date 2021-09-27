<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
$title=isset($_POST['title']) ? $_POST['title'] : '';
$rating=isset($_POST['rating']) ? $_POST['rating'] : '';
$comment=isset($_POST['comment']) ? $_POST['comment'] : '';
$id=isset($_POST['id']) ? $_POST['id'] : '';
try{
	  $sql = "INSERT INTO $title (storecomment, storeRating, userID) VALUES('$comment', '$rating', '$id')";
                $stmt = $con->prepare($sql);
                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }
            } catch(PDOException $e) {
	  echo "fail";
              
            }
 }
?>