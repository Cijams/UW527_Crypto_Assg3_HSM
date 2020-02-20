package springboot;

import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main method that all cryptographic functions are performed in.
 */
@RestController
public class CryptoController {

	// ****************************       EXAMPLE          **************************************

	// FOLLOW THIS FORMAT AND YOU CAN DO ANYTHING. WILL DO A POST REQUEST EXAMPLE ASAP.
	// FLUFF
	@GetMapping
	@RequestMapping("/encrypt-example")

	// METHOD
	public Map<String, String> encryptExample() {
		// MAP FOR RETURN VALUES. CAN DO RAW VALUES TOO, OBJECTS ARE AUTO JSON'D GOING BACK TO CLIENT
		HashMap<String, String> All_MY_DATA = new HashMap<>();

		// DO SOME COOL CRYPTO
		int test = 500;
		test = test % 9 * 4;

		// RETURN IT
		All_MY_DATA.put("key", test + "");
		return All_MY_DATA;
	}

	// *******************************************************************************************

	/**
	 * TO-DO - Decide which of these will be post or get requests and perform the respectful crypto operation.
	 */

	@RequestMapping("/")
	public String index() {
		return "Server Up";
	}

	@CrossOrigin
	@GetMapping("/hash")
	public ResponseEntity<String> hash() {
		return new ResponseEntity<String>("Response from the hash method", HttpStatus.OK);
	}

	// @CrossOrigin
	// @ResponseBody
	// @PostMapping("/login")
	// public String login() {
	// }

	@CrossOrigin
	@GetMapping("/register")
	public ResponseEntity<String> register() {
		return new ResponseEntity<String>("Response from the register method", HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/genKeys")
	public ResponseEntity<String> genKeys() {
		return new ResponseEntity<String>("Response from the genKeys method", HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/getKeys")
	public ResponseEntity<String> getKeys() {
		return new ResponseEntity<String>("Response from the getKeys method", HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/decrypt")
	public ResponseEntity<String> decrypt() {
		return new ResponseEntity<String>("Response from the decrypt method", HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/sign")
	public ResponseEntity<String> sign() {
		return new ResponseEntity<String>("Response from the sign method", HttpStatus.OK);
	}

}
