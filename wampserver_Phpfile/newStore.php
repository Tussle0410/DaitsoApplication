<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 
include('dbcon2.php');
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
$title=isset($_POST['title']) ? $_POST['title'] : '';
$address=isset($_POST['address']) ? $_POST['address'] : '';
$kinds=isset($_POST['kinds']) ? $_POST['kinds'] : '';
$region=isset($_POST['region']) ? $_POST['region'] : '';
try{
                $stmt = $con->prepare('INSERT INTO store(storeName, storeAddress, storeKinds,region) VALUES(:storeName, :storeAddress, :storeKinds,:region)');
                $stmt->bindParam(':storeName', $title);
                $stmt->bindParam(':storeAddress', $address);
                $stmt->bindParam(':storeKinds', $kinds);
                $stmt->bindParam(':region', $region);
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