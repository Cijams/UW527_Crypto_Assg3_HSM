package springboot;

import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

	// **************************** EXAMPLE **************************************

	// FOLLOW THIS FORMAT AND YOU CAN DO ANYTHING. WILL DO A POST REQUEST EXAMPLE
	// ASAP.
	// FLUFF
	@GetMapping
	@RequestMapping("/encrypt-example")

	// METHOD
	public Map<String, String> encryptExample() {
		// MAP FOR RETURN VALUES. CAN DO RAW VALUES TOO, OBJECTS ARE AUTO JSON'D GOING
		// BACK TO CLIENT
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
	 * TO-DO - Decide which of these will be post or get requests and perform the
	 * respectful crypto operation.
	 */

	/**
	 * Basic return data for home page.
	 * 
	 * @return None
	 */
	@RequestMapping("/")
	public String index() {
		return "Server Up";
	}

	/**
	 * Generates a SHA-256 value.
	 */
	@CrossOrigin
	@GetMapping("/hash")
	public ResponseEntity<String> hash() throws NoSuchAlgorithmException {
		byte[] sha2 = new byte[0];
		StringBuilder hexString = null;
		sha2 = generateSha2(Math.random() + ""); // Accep user input
		// Convert SHA-2 to hexadecimal
		hexString = _generateHex(sha2);
		return new ResponseEntity<String>(hexString.toString(), HttpStatus.OK);
	}

	/**
	 * Generate the SHA-256 value.
	 * 
	 * @param input A string representing the seed value
	 * @return A byte array represending the hash in UTF-8 format.
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateSha2(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns the hexidecimal format of the incoming byte array.
	 * 
	 * @param bytes A byte array representation of a SHA-256 hash.
	 * @return The hexidecimal representation of a SHA-256 hash.
	 */
	private static StringBuilder _generateHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(String.format("%02X", b));
		}
		return hexString;
	}

	// @CrossOrigin
	// @ResponseBody
	// @PostMapping("/login")
	// public String login() {
	// }

	/**
	 * Registers a user with a password to the HSM database.
	 * 
	 * @param User ID, User Password
	 * @return boolean of the acceptance status of a register request.
	 */
	@CrossOrigin
	@GetMapping("/register")
	public ResponseEntity<String> register() {
		return new ResponseEntity<String>("Response from the register method", HttpStatus.OK);
	}

	/**
	 * Login request for the user. Hash of the user password is compared to the one
	 * stored in the HSM DB.
	 * 
	 * @param User ID, User Password
	 * @return boolean of the acceptance status of a login request.
	 */
	@CrossOrigin
	@GetMapping("/login")
	public ResponseEntity<String> login() {
		return new ResponseEntity<String>("Response from the login method", HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/genKeys")
	public ResponseEntity<String> genKeys() {
		return new ResponseEntity<String>("Response from the genKeys method", HttpStatus.OK);
	}

	/**
	 * Locates the private key corresponding to the provided Key ID. Returns the
	 * encryption of the provided text.
	 * 
	 * @param Text, Key ID, Key Password
	 * @return RSA(Text, Private Key from HSM DB)
	 */
	@CrossOrigin
	@GetMapping("/encrypt")
	public ResponseEntity<String> encrypt() {
		return new ResponseEntity<String>("Response from the encrypt method", HttpStatus.OK);
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

	@CrossOrigin
	@GetMapping("/displayKeys")
	public ResponseEntity<String> displayKeys() {
		return new ResponseEntity<String>("Response from the displayKeys method", HttpStatus.OK);
	}

	/**
	 * Generates a report summarizing the status of the HSM. A list of the
	 * registered users and stored keys.
	 */
	@CrossOrigin
	@GetMapping("/genReport")
	public ResponseEntity<String> genReport() {
		return new ResponseEntity<String>("Response from the genReport method", HttpStatus.OK);
	}

}