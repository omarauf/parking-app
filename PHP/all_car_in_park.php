<?php
//this page used in park activity to get all the car for specific park
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['park_id']) ) {
 
    // receiving the post params
    $park_id = intval($_POST['park_id']);   //the park ID to get all the car in it
 
    // get all the car in this park by park ID
    $cars_in_park = $db->getAllCarsbyParkID($park_id);
	
    if ($cars_in_park != false) {
        // cars are found
        $response["error"] = FALSE;
        $response["cars"] = $cars_in_park;
        echo json_encode($response);
    } else {
        // there is no car in this park 
        $response["error"] = TRUE;
        $response["error_msg"] = "No Car";
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
                <label for="park_id">park_id</label>
                <input type="park_id" class="form-control" id="park_id" name="park_id" placeholder="park_id">
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