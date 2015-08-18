<?php

$con=mysql_connect("localhost","snapxyco_kh","qwe123qwe123");

if(!$con)
{
die('Error 1 :'.mysql_error());
}

mysql_select_db("beacondemo",$con);


$sql="INSERT INTO beacon_info(uuid,major,minor,regid)
VALUES
('$_POST[uuid]','$_POST[major]','$_POST[minor]','$_POST[regid]')";

  $files = glob('image/*.*');
    $file = array_rand($files);
   // echo $files[$file];
    

    $json = array();
    
    $json[] = array(
        'uuid' => $_POST[uuid],
        'major' => $_POST[major],
	'minor' => $_POST[minor],
	'regid' => $_POST[regid],
	'isDetectBeacon' => $_POST[isDetectBeacon],
        'Image_URL'=>$files[$file],
        'Message'=> 'Discount 30 %'
        );


if(!mysql_query($sql,$con))
{
//die('Error 2 :'.mysql_error());
echo json_encode($json, JSON_UNESCAPED_SLASHES);
}
else
{

    echo json_encode($json, JSON_UNESCAPED_SLASHES);

}
mysql_close($con);

?>