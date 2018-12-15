<?php
// this is a page to update database for specific car to specific park at specific time 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['park_in']) && isset($_POST['car_id']) ) {
 
    // receiving the post params
    $park_in = intval($_POST['park_in']);   //where car want to park
    $car_id = intval($_POST['car_id']);     //at what time the car park to calucate the price
 
    // get the park detail and decrease the park by 1
    $parkDetail = $db->parkCarbyParkID($park_in, $car_id);
	$db->updateParkCapacity($park_in);
 
    if ($parkDetail != false) {
        // you park here 
        $response["error"] = FALSE;
        $response["park_in"] = $parkDetail["park_in"]; //Park ID where car is parked
        $response["park_at"] = $parkDetail["park_at"]; //Time where car is parked
        echo json_encode($response);
    }
}
?>
<!doctype html>
<html lang="en">

<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
        crossorigin="anonymous">

    <title>Login</title>
</head>

<body>


    <div class="container">
        <h1>Get in touch!</h1>
        <!<div id="error"><? echo $error.$successMessage; ?></div>
        <form method="post">
             <!--Emain Password-->
            <div class="form-group">
                <label for="latitude">latitude</label>
                <input type="latitude" class="form-control" id="latitude" name="latitude" placeholder="latitude">
            </div>
            <!--Emain Address-->
            <div class="form-group">
                <label for="longitude">longitude</label>
                <input type="longitude" class="form-control" id="longitude" name="longitude" aria-describedby="emailHelp" placeholder="Enter your longitude">
            </div>
           
			
            <button type="submit" id="submit" class="btn btn-primary">Submit</button>
        </form>
    </div>


    <!-- Optional JavaScript -->
    <!-- jQuery first, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
     
    <script type="text/javascript">
	
	
        $("form").submit(function(e) {
            var error = "";
            if ($("#email").val() == "") {
                error += "The email field is required.<br>"
            }
            if ($("#password").val() == "") {
                error += "The password field is required.<br>"
            }
            if (error != "") {
                $("#error").html('<div class="alert alert-danger" role="alert"><p><strong>There were error(s) in your form:</strong></p>' + error + '</div>');
                return false;
            } else {
                return true;
            }
        });


    </script>

</body>

</html>