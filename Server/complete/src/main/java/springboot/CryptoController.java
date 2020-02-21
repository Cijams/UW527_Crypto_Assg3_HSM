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
	public ResponseEntity<String> hash() throws NoSuchAlgorithmException {
		byte[] sha2 = new byte[0];
		StringBuilder hexString = null;
		sha2 = generateSha2(Math.random()+"");
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
