<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
$title=isset($_POST['title']) ? $_POST['title'] : '';
$comment=isset($_POST['comment']) ? $_POST['comment'] : '';
$userID=isset($_POST['userID']) ? $_POST['userID'] : '';
try{
                $stmt = $con->prepare('INSERT INTO inquiry(userID, inquiryTitle, inquiryComment) VALUES(:userID, :title, :comment)');
                $stmt->bindParam(':userID', $userID);
                $stmt->bindParam(':title', $title);
                $stmt->bindParam(':comment', $comment);
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
?>