<?php
 
/**
 * @author Ravi Tamada
 * @link https://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */
 
class DB_Functions {
 
    private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password, $type, $phone) {
        //$uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
 
        $stmt = $this->conn->prepare("INSERT INTO `users`(name, email, encrypted_password, salt, type, phone, created_at) VALUES(?, ?, ?, ?, ?, ?, NOW())"); // add type by omar
        $stmt->bind_param("ssssss", $name, $email, $encrypted_password, $salt, $type, $phone); // add and s by omar
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    } 

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
 
        $stms = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
 
        $stms->bind_param("s", $email);
 
        if ($stms->execute()) {
            $user = $stms->get_result()->fetch_assoc();
            $stms->close();
 
            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }

	/**
     * Storing new cars
     * returns car details
     */
    public function storeCar($plate, $body, $color, $company, $carName, $id) {
 
        $stmt = $this->conn->prepare("INSERT INTO cars(`user_id`, `plate`, `body`, `color`, `company`, `name`) VALUES(?, ?, ?, ?, ?, ?)"); 
        $stmt->bind_param('isssss', $id, $plate, $body, $color, $company, $carName);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM `cars` WHERE user_id = ? ORDER BY `car_id` DESC LIMIT 1");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            $car = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $car;
        } else {
            return false;
        }
    }

	/**
     * Storing new park
     * returns park details
     */
    public function storePark($latitude, $longitude, $capacity, $parkName, $price, $id) { 
 
        $stmt = $this->conn->prepare("INSERT INTO parks(user_id, latitude, longitude, capacity, `name`, price) VALUES(?, ?, ?, ?, ?, ?)"); 
        $stmt->bind_param('iddisi', $id, $latitude, $longitude, $capacity, $parkName, $price); 
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM `parks` WHERE user_id = ? ORDER BY `parker_id` DESC LIMIT 1");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            $park = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $park;
        } else {
            return false;
        }
    }

    /**
     * in login page to get park for user
     */
    public function getParksByUserID($id) {
        $stmt = $this->conn->prepare("SELECT parker_id, latitude, longitude, capacity, `name`, price FROM `parks` WHERE user_id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $park = $stmt->get_result()->fetch_assoc();
        $stmt->close();
        
        if ($park) {
            return $park;
        } else {
            return false;
        }
        
    }

     /**
     * in login page to get car for user
     */
    public function getCarsByUserID($id) {
        $stmt = $this->conn->prepare("SELECT `car_id`, `plate`, `body`, `color`, `company`, `name` FROM `cars` WHERE `user_id` =  ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $car = $stmt->get_result()->fetch_assoc();
        $stmt->close();
        
        // check for successful store
        if ($car) {
            return $car;
        } else {
            return false;
        }
    }
	

    /**
     * returns Nearest Park beased on specific latitude and longitude which are the car location
     */
    public function getNearestPark($latitude, $longitude) { 

        $stms = $this->conn->prepare("SELECT * FROM `parks` ORDER BY ((? - latitude)*(? - latitude)) + ((? -  longitude)*(? - longitude)) ASC"); 
        $stms->bind_param('dddd', $latitude, $latitude, $longitude, $longitude); 
        if ($stms->execute()) {
			$result = $stms->get_result();
			while ($data = $result->fetch_assoc()) {
				$nearestParks[] = $data;
			}
            $stms->close();
			return $nearestParks;
        } 
    }

    /**
     * returns all cars in specific park by it's ID
     */
    public function getAllCarsbyParkID($park_id) { 
        $stms = $this->conn->prepare("SELECT * FROM `cars` where `park_in` = ?"); 
        $stms->bind_param('i', $park_id); 
        if ($stms->execute()) {
			$result = $stms->get_result();
			while ($data = $result->fetch_assoc()) {
				$cars_in_park[] = $data;
			}
            $stms->close();
			return $cars_in_park;
        } 
    }

    /**
     * check out car by id and incresse capacity park
     */
    public function checkOutCarByCarID($car_id, $park_id) { 
 
        $stmt = $this->conn->prepare("SELECT `park_at` FROM `cars` WHERE `car_id`= ?"); 
        $stmt->bind_param('i', $car_id); 
        $stmt->execute();
        $date = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        $stmt = $this->conn->prepare("UPDATE `cars` SET `park_in` = null, `park_at` = 0 WHERE `car_id`= ?"); 
        $stmt->bind_param('i', $car_id); 
        $result = $stmt->execute();
        $stmt->close();

        $stmt = $this->conn->prepare("UPDATE `parks` SET `capacity` = capacity + 1 WHERE `parker_id` = ?");
        $stmt->bind_param("i", $park_id); 
		$stmt->execute();
        $stmt->close();

        return $date;
    }

     /**
     * park a car by it's ID at specific by park by it's ID
     */
    public function parkCarbyParkID($park_in, $car_id) { 
 
        $stmt = $this->conn->prepare("UPDATE `cars` SET `park_in` = ?, `park_at` = NOW() WHERE `car_id`= ?"); 
        $stmt->bind_param('ii', $park_in, $car_id); 
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM `cars` WHERE `car_id` = ?");
            $stmt->bind_param("i", $car_id);
            $stmt->execute();
            $car = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $car;
        } else {
            return false;
        }
    }

    /**
     * decresse capacity for park by 1 when car request a park
     */
    public function updateParkCapacity($park_id) {
        $stms = $this->conn->prepare("UPDATE `parks` SET `capacity` = capacity - 1 WHERE `parker_id` = ?");
        $stms->bind_param("i", $park_id); 
		$stms->execute();
    }	
	
    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stms = $this->conn->prepare("SELECT email from users WHERE email = ?");
 
        $stms->bind_param("s", $email);
 
        $stms->execute();
 
        $stms->store_result();
 
        if ($stms->num_rows > 0) {
            // user existed 
            $stms->close();
            return true;
        } else {
            // user not existed
            $stms->close();
            return false;
        }
    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
 
}
 
?>