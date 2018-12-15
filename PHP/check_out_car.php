<?php
//this page used in park activity to check out specific car and calucate parking price 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);

if (isset($_POST['car_id']) && isset($_POST['park_id']) && isset($_POST['price'])) {
 
    // receiving the post params
    $car_id = intval($_POST['car_id']);
    $park_id = intval($_POST['park_id']);
    $price = intval($_POST['price']);
 
    // check out car by car ID to
    $cars_in_park = $db->checkOutCarByCarID($car_id, $park_id);
 
    if ($cars_in_park != false) {
        $response["error"] = FALSE;
        $response["date"] = $cars_in_park["park_at"];
        // calucate the price and return it 
        $start  = date_create($response["date"]);
        $end 	= date_create(); // Current time and date
        $diff  	= date_diff( $start, $end );
        $year = $diff->y;
        $month = $diff->m;
        $day = $diff->d;
        $hours = $diff->h;
        $minute = $diff->i;
        $second = $diff->s;
        if ($minute > 0){
            $hours = $hours + 1;
        }
        $Total = $hours * $price;
        $response["price"] = $Total;
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

            <div class="form-group">
                <label for="car_id">car_id</label>
                <input type="car_id" class="form-control" id="car_id" name="car_id" placeholder="car_id">
            </div>      

            <div class="form-group">
                <label for="price">price</label>
                <input type="price" class="form-control" id="price" name="price" placeholder="price">
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