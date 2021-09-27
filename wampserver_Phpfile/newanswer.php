<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
$answer=isset($_POST['answer']) ? $_POST['answer'] : '';
$no=isset($_POST['no']) ? $_POST['no'] : '';
try{
	  $sql = "INSERT INTO inquiryanswer(answer, inquiryNo) VALUES('$answer', '$no')";
                $stmt = $con->prepare($sql);
                if($stmt->execute())
                {
                    $successMSG = "새로운 답변이 추가했습니다.";
                }
                else
                {
                    $errMSG = "답변 추가 에러";
                }
            } catch(PDOException $e) {
	  echo "fail";
              
            }
 }
?>