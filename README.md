# Parker

Parker is an app that allows the customer to search for nearest parking based on customer location.

2 subprograms:
  - Driver
  - Park
 
# Usage!

This app consist of two subprograms Driver, Park. The program starts by login screen which asks the user for an email and password or to create a new account if he hasn't an account. If the clients haven’t account he/she will create a new account by entering a name, email password and phone number. Then, the client has to choose if the preferred account is driver account or parking account.

![](https://raw.githubusercontent.com/omarauf/parking-app/master/Pic%20Example/Driver.jpg)
*As Driver:* if the client chooses Driver account the client should take the picture for his/her car including the plate so the system will recognize the plate and save it the user will not be able to write his/her plate due to a security issue. The user will click register button so the system will create an account with email and password that he/she chose and register the car with car plate, colour, bodysize and company information that extract from the pic that he/she took. Then the program will redirect to the login page where the client will enter his/her credentials and based on his/her account type will be redirected to car activity. In car activity the user will click the find park button and the program will check all the available parking and return them as list sorted based on his location each park has its own price and capacity then the user will click on the preferred park pop up will show and ask him/her if he/she wants to park in selected park if he/she choose yes then the program will redirect to another screen that shows the park name and the time that he/she parked his/her car and button that will open google maps and take him/her to park location 


![](https://raw.githubusercontent.com/omarauf/parking-app/master/Pic%20Example/Park.jpg)
*As Park:* If he/she chose the parking account then he/she has to be in the location of the park to ensure that he/she and the system will choose the correct location so he/she will click get set location button as also he/she will write the park name and specify the capacity and the price for one hour. The user will click register button so the system will create an account with email and password that he/she chose and register the park with park locations as longitude and latitude data and park name and its capacity will be saved. Then the program will redirect to the login page where the client will enter his/her credentials and based on his/her account type will be redirected to park activity. In park activity the park owner will have a button to refresh his page and get all the cars plate that it in his/her park if a car owner wants to check out a car he/she should find the car plate in the list that matches the car’s plate and clicked on it a pop up will show and ask him/her if he/she wants to check out this car if he click yes then a price will show based on park price and parked time and the car owner are free to go 



### Tech
Parker app uses a number of open source projects to work properly:

* [Bootstrap 4] - Bootstrap is an open source toolkit for developing with HTML, CSS, and JS.
* [jQuery] - jQuery is a fast, small, and feature-rich JavaScript library.
* [PHP] - Hypertext Preprocessor is a server-side scripting language designed for Web development.
* [MySQL] - MySQL is an open source relational database management system.
* [AndroidHive] - Login and Registration with PHP, MySQL.
* [openALPR] - automatic number-plate recognition library written in C++.
* [Google Maps] - DescriptionGoogle Maps is a web mapping service developed by Google.
* [Volley] - Volley is an HTTP library that makes networking for Android apps easier and most importantly, faster.
### Installation

just put database credentials in include/Config.php file

credit: https://github.com/Drooz

   [Bootstrap 4]: <https://getbootstrap.com/>
   [jQuery]: <http://jquery.com>
   [php]: <http://twitter.com/tjholowaychuk>
   [mysql]: <https://www.mysql.com/>
   [androidhive]: <https://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/>
   [openALPR]: <https://www.openalpr.com/> 
   [Google Maps]: <https://developers.google.com/maps/documentation/>
   [Volley]: <https://developer.android.com/training/volley/>
   
   

