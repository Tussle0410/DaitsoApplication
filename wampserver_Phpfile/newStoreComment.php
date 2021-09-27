<?php
$servername = "localhost";
$username = "lee";
$password = "2132";
$dbname = "store";
 
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
$title=isset($_POST['title']) ? $_POST['title'] : '';
// sql to create table
$sql = "CREATE TABLE $title (
storecomment varchar(250) NOT NULL,
storeRating int not null default 0,
userID varchar(25) not null primary key)ENGINE=InnoDB DEFAULT CHARSET=utf8";
 
if ($conn->query($sql) === TRUE) {
    echo "Table MyGuests created successfully";
} else {
    echo "Error creating table: " . $conn->error;
}
 
$conn->close();
?>
