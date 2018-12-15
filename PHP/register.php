<?php
 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['type']) && isset($_POST['phone'])) {  
 
    // receiving user from post params
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $type = $_POST['type'];
	$phone = $_POST['phone']; 

    // check if user is already existed with the same email
    if ($db->isUserExisted($email)) {
        // user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "User already existed with " . $email;
        echo json_encode($response);
    } else {
        // create a new user
        $user = $db->storeUser($name, $email, $password, $type, $phone);  
        if ($user) {
			// user stored successfully
            $response["error"] = FALSE;	
			$response["id"] = $user["id"];
			$response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["type"] = $user["type"]; 
			$response["user"]["phone"] = $user["phone"]; 
			$response["user"]["own"] = $user["own"]; 
			$response["user"]["created_at"] = $user["created_at"];
			if ($type == "car"){
				// receiving car detail from post params
				$plate = $_POST['plate'];
				$body = $_POST['body'];
				$color = $_POST['color'];
				$company = $_POST['company']; 
                $carName = $_POST['carName']; 
                //Store Car
                $car = $db->storeCar($plate, $body, $color, $company, $carName, $user["id"]);
                // car stored successfully
				$response["car_id"] = $car["car_id"];
				$response["car"]["plate"] = $car["plate"];
				$response["car"]["body"] = $car["body"];
				$response["car"]["color"] = $car["color"];
				$response["car"]["company"] = $car["company"];
			}else if($type == "park"){
                // receiving park detail from post params
				$latitude = floatval($_POST['latitude']); // double
				$longitude = floatval($_POST['longitude']); // double
				$parkName = $_POST['parkName']; 
				$capacity = intval($_POST['capacity']); // int
                $price = intval($_POST['price']); // int
                //Store park
                $park = $db->storePark($latitude, $longitude, $capacity, $parkName, $price, $user["id"]); 
                // park stored successfully
				$response["park_id"] = $park["parker_id"];
				$response["park"]["latitude"] = $park["latitude"];
				$response["park"]["longitude"] = $park["longitude"];
				$response["park"]["capacity"] = $park["capacity"];
				$response["park"]["parkName"] = $park['name']; 
				$response["park"]["price"] = $park['price']; 
			}
			echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters is missing!";
    echo json_encode($response);
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
                <label for="name">name</label>
                <input type="name" class="form-control" id="name" name="name" placeholder="name">
            </div>
            <!--Emain Address-->
            <div class="form-group">
                <label for="email">Email address</label>
                <input type="text" class="form-control" id="email" name="email" aria-describedby="emailHelp" placeholder="Enter your email">
            </div>
            <!--Emain Password-->
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Password">
            </div>
			
            <div class="form-group">
                <label for="phone">phone</label>
                <input type="phone" class="form-control" id="phone" name="phone" placeholder="phone">
            </div>

            
			
			
			<!--
            <div class="form-group">
                <label for="type">type</label>
                <input type="type" class="form-control" id="type" name="type" placeholder="type">
            </div>
			-->
			<div class="form-group">
				<label for="type">Type</label>
				<select id="type" name="type" class="form-control">
					<option selected value="car">car</option>
					<option value="park">park</option>
				</select>
			</div>
			<div class="carorpark">
			
			</div>
			
		<!--
            <div class="form-group">
                <label for="plate">plate</label>
                <input type="text" class="form-control" id="plate" name="plate" placeholder="plate">
            </div>
        
            <div class="form-group">
                <label for="body">body</label>
                <input type="text" class="form-control" id="body" name="body"  placeholder="body">
            </div>
         
            <div class="form-group">
                <label for="color">color</label>
                <input type="text" class="form-control" id="color" name="color" placeholder="color">
            </div>
			
            <div class="form-group">
                <label for="company">company</label>
                <input type="text" class="form-control" id="company" name="company" placeholder="company">
            </div>
			-->
			
			
            <button type="submit" id="submit" class="btn btn-primary">Submit</button>
        </form>
    </div>


    <!-- Optional JavaScript -->
    <!-- jQuery first, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
     
    <script type="text/javascript">
	
		$('select').on('change', function() {
			if(this.value == "car"){
				$( "div.carorpark" ).html('<div class="form-group">                 <label for="plate">plate</label> 				                 <input type="text" class="form-control" id="plate" name="plate" placeholder="plate">             </div>                      <div class="form-group">                 <label for="body">body</label>                 <input type="text" class="form-control" id="body" name="body"  placeholder="body">             </div>                       <div class="form-group">                 <label for="color">color</label>                 <input type="text" class="form-control" id="color" name="color" placeholder="color">             </div> 			             <div class="form-group">                 <label for="company">company</label>                 <input type="text" class="form-control" id="company" name="company" placeholder="company">             </div>')
			}
			 else if(this.value == "park"){
				$( "div.carorpark" ).html('<div class="form-group">                 <label for="latitude">latitude</label>                 <input type="text" class="form-control" id="latitude" name="latitude" placeholder="latitude">             </div>                      <div class="form-group">                 <label for="longitude">longitude</label>                 <input type="text" class="form-control" id="longitude" name="longitude"  placeholder="longitude">             </div>                       <div class="form-group">                 <label for="capacity">capacity</label>                 <input type="text" class="form-control" id="capacity" name="capacity" placeholder="capacity">             </div>  <div class="form-group">                <label for="parkName">parkName </label>                <input type="parkName" class="form-control" id="parkName" name="parkName" placeholder="parkName">            </div>            <div class="form-group">                <label for="price">price </label>                <input type="price" class="form-control" id="price" name="price" placeholder="price">            </div>')
			 }	
		});
    
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