<?php

// this page used by car activity to find all nearest park 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['latitude']) && isset($_POST['longitude'])) {
 
    // receiving the post params these are the location of car when it request to find park
    $car_latitude = $_POST['latitude'];
    $car_longitude = $_POST['longitude'];
 
    // get the nearest park in park sorted JSON Array 
    $nearestParks = $db->getNearestPark($car_latitude, $car_longitude);
	
    if ($nearestParks != false) {
        // parks are found
        $response["error"] = FALSE;
		$response["nearestParks"] = $nearestParks;
        echo json_encode($response);
    } else {
        // there is no park 
        $response["error"] = TRUE;
        $response["error_msg"] = "There is no park in area. Please try again!";
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