<?php 
require_once ('../pdo_config.php'); // Connect to the db.

if (isset($_POST['Register'])){ //they call the function they want to use in the post method
	$username = trim(filter_input(INPUT_POST, 'UserName', FILTER_SANITIZE_STRING)); //returns a string
	$valid_email = trim(filter_input(INPUT_POST, 'email', FILTER_VALIDATE_EMAIL));	//returns a string or null if empty or false if not valid	
	$password = trim(filter_input(INPUT_POST, 'password', FILTER_SANITIZE_STRING)); //returns a string
	$sql = "INSERT into User (UserName,Password,Balance,Email) VALUES (:UserName, :Password,:Balance,:Email)"; //setting up prepared statements for more safety
	$stmt= $conn->prepare($sql);
	$stmt->bindValue(':userName', $username);
	$stmt->bindValue(':Password', password_hash($password, PASSWORD_DEFAULT)); //we hash the password to protect users info
	$stmt->bindValue(':Balance', 5000); //starting users with a blance of 5000
	$stmt->bindValue(':Email', $valid_email);
	$success = $stmt->execute();
	$errorInfo = $stmt->errorInfo();		
	if (isset($errorInfo[2])){ //if there is an error echo the error
			echo $errorInfo[2];
			exit;
		}
	else{
		echo("User successfully registered");
	}
}

if (isset($_POST['Login'])){ //they called the function to login in the post method
	$username = trim(filter_input(INPUT_POST, 'username', FILTER_SANITIZE_STRING)); //returns a string
	$sql = "SELECT UserName, Password,Balance, Email FROM User WHERE UserName = :UserName"; //prepared statements for safety
		$stmt = $conn->prepare($sql);
		$stmt->bindValue(':UserName', $username); 
		$stmt->execute();
		$errorInfo = $stmt->errorInfo();
		if (isset($errorInfo[2])){ //print any erros that may come up with the statement
			echo $errorInfo[2];
			exit;
		}
		else { //if their are no rows username must not exist in the database
			$rows = $stmt->rowCount();
			if ($rows==0) { //username not found
				echo"UserName not found";
			}
			else { // Username found, validate password
				$result = $stmt->fetch();
				if ($password == password_verify($password, $result['Password'])) { //passwords match
					echo"Login Successful";
				}
				else {
					echo"Password is incorrect";
					}
				}
			}
}

if (isset($_POST['BuyStock'])){ //they called the function to BuyStock in the post method
	$username = trim(filter_input(INPUT_POST, 'UserName', FILTER_SANITIZE_STRING)); //returns a string
	$stockname = trim(filter_input(INPUT_POST, 'StockName', FILTER_SANITIZE_STRING));	//returns a string
	$boughtprice = trim(filter_input(INPUT_POST, 'BoughtPrice', FILTER_SANITIZE_STRING)); //returns a string
	$quantity = trim(filter_input(INPUT_POST, 'Quantity', FILTER_SANITIZE_STRING)); //returns a string
	//sql to update the STOCK table with the new Data
	$sql = "INSERT into STOCK (UserName,StockName,BoughtPrice, Quantity) VALUES (:UserName, :StockName,:BoughtPrice,:Quantity)"; //setting up prepared statements for more safety
	$stmt= $conn->prepare($sql);
	$stmt->bindValue(':UserName', $username);
	$stmt->bindValue(':StockName', $stockname);
	$stmt->bindValue(':BoughtPrice', $boughtprice);
	$stmt->bindValue(':Quantity', $quantity);
	$errorInfo = $stmt->errorInfo();		

	$spent = (int)$boughtprice * (int)$quantity;
	//Grabs the balance from the Database
	$sql = "Select Balance from User Where UserName = :UserName";
	$stmt= $conn->prepare($sql);
	$stmt->bindValue(':UserName', $username);
	$stmt->execute();
	$result = $stmt->fetch();
	
	// calculate how much you spent and update your balance to reflect these changes
	$newBalance = $result["Balance"] - $spent;
	$sql = "UPDATE USER Set Balance = :Balance WHERE UserName = :UserName";
	$stmt= $conn->prepare($sql);
	$stmt->bindValue(':Balance', $newBalance);
	$stmt->bindValue(':UserName', $username);
	$stmt->execute();
	if (isset($errorInfo[2])){ //if there is an error echo the error
		echo $errorInfo[2];
		exit;
		}
	else{
		echo("Stock Successfully bought");
	}
}

if (isset($_POST['SellStock'])){ //Post method to sell a certain amount of stock
	$username = trim(filter_input(INPUT_POST, 'UserName', FILTER_SANITIZE_STRING)); //returns a string
	$quantity = trim(filter_input(INPUT_POST, 'Quantity', FILTER_SANITIZE_STRING)); //returns a string
	$stockname = trim(filter_input(INPUT_POST, 'StockName', FILTER_SANITIZE_STRING));	//returns a string
	$current_price = trim(filter_input(INPUT_POST, 'CurrentPrice', FILTER_SANITIZE_STRING)); //returns a string
	
	//Grab from the database the quantity of stock you had
	$sql = "Select BoughtPrice Quantity FROM STOCK Where UserName = :UserName and StockName=: StockName";
	$stmt= $conn->prepare($sql);
	$stmt->bindValue(':UserName', $username);
	$stmt->bindValue(':StockName', $stockname);
	$stmt->execute();
	$result = $stmt->fetch();
	
	// If you tried to buy more than you own Error
	if($result["Quantity"] < $quantity ){
		echo "You Only Own a quantity of " . $result["Quantity"];
		break;
	}
	else{
		//If you are trying to sell all of your quanitiy of the said stock
		if($result["Quantity"] = $quantity ){
			//Proft is how much money you will make selling that stock
			$profit = $quantity * $current_price;
			$sql = "DELETE FROM STOCK WHERE UserName= :UserName and StockName = :StockName";
			$stmt= $conn->prepare($sql);
			$stmt->bindValue(':UserName', $username);
			$stmt->bindValue(':StockName', $stockname);
			$stmt->execute();
			
			//Update the user table with the new balance
			$sql = "Update User Set Balance = Balance + :Profit Where UserName =:UserName";
			$stmt= $conn->prepare($sql);
			$stmt->bindValue(':UserName', $username);
			$stmt->bindValue(':Profit', $profit);
			$stmt->execute();
			echo "Stock Sold";
		}
		else{
			// if you are only selling some of your stock
			//Proft is how much money you will make selling that stock
			$profit = $quantity * $current_price;
			// Find the new quantity of stock you now own
			$new_quantity = $quantity - $result["Quantity"];
			// Update the stock table to only show your new quantity
			$sql = "Update Stock SET quantity =:Quantity WHERE UserName= :UserName and StockName = :StockName";
			$stmt= $conn->prepare($sql);
			$stmt->bindValue(':UserName', $username);
			$stmt->bindValue(':Quantity', $new_quantity);
			$stmt->bindValue(':StockName', $stockname);
			$stmt->execute();
			//Update user table with new Balance
			$sql = "Update User Set Balance = Balance + :Profit Where UserName =:UserName";
			$stmt= $conn->prepare($sql);
			$stmt->bindValue(':UserName', $username);
			$stmt->bindValue(':Profit', $profit);
			$stmt->execute();
			echo "Stock Sold";
		}
	}
}

?>
