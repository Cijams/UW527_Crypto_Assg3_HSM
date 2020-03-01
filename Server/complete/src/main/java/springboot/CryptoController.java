package springboot;

import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Main method that all cryptographic functions are performed in.
 */
@RestController
public class CryptoController {

	static String username = "";
	@Autowired
	private PersistenceService service;
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
	public ResponseEntity<String> hashWeb() throws NoSuchAlgorithmException {
		byte[] sha2 = new byte[0];
		StringBuilder hexString = null;
		sha2 = generateSha2(Math.random() + ""); // Accep user input
		// Convert SHA-2 to hexadecimal
		hexString = _generateHex(sha2);
		return new ResponseEntity<String>(hexString.toString(), HttpStatus.OK);
	}

	public String hash(String input) throws NoSuchAlgorithmException {
		byte[] sha2 = new byte[0];
		StringBuilder hexString = null;
		sha2 = generateSha2(input);
		hexString = _generateHex(sha2);
		return hexString.toString();
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

	// /**
	// * Registers a user with a password to the HSM database.
	// *
	// * @param User ID, User Password
	// * @return boolean of the acceptance status of a register request.
	// */
	// @CrossOrigin
	// @GetMapping("/register")
	// public ResponseEntity<String> register() {
	// // User user = new User();
	// // user.setUserName("Henry");
	// // user.setPasswordHash(passwordHash);
	// return new ResponseEntity<String>("Response from the register method",
	// HttpStatus.OK);
	// }

	/**
	 * Login request for the user. Hash of the user password is compared to the one
	 * stored in the HSM DB.
	 * 
	 * @param User ID, User Password
	 * @return boolean of the acceptance status of a login request.
	 */

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
	 * registered users and their associated stored keys.
	 */
	@CrossOrigin
	@GetMapping("/generateReport")
	public Map<String, String> genReport() {
		LinkedHashMap<String, String> reportData = new LinkedHashMap<>();

		List users = null;
		List keys = null;
		User aUser = null;

		users = service.findAllUsers();

		for(int i = 0; i < users.size(); i++) {
			String addedKey = "";
			aUser = (User) users.get(i);
			keys = aUser.getKeys();
			if(keys != null && !keys.isEmpty()) {
				addedKey = aUser.getKeys().toString();
			}
			reportData.put(aUser+"", addedKey);
		}

		System.out.println(reportData.toString());
		return reportData;
	}

	/**
	 * Registers a new user in the HSM DB. User password is stored hashed in the DB.
	 * 
	 * @param userID   The name identification field associated with a user.
	 * @param password The chosen password of the user.
	 * @return A
	 * @throws NoSuchAlgorithmException
	 */
	@CrossOrigin
	@GetMapping("/registerUser")
	@ResponseBody
	public Map<String, Boolean> registerUser(@RequestParam String userID, @RequestParam String password)
			throws NoSuchAlgorithmException {
		HashMap<String, Boolean> data = new HashMap<>();
		if (service.getUserByUsername(userID) != null) {
			data.put(userID, false);
			return data;
		}
		System.out.println("User name:" + userID + "\nPassword: " + password);
		User user = new User();
		user.setUserName(userID);
		String hash = this.hash(password);
		user.setPasswordHash(hash);
		service.createUser(user.getUserName(), user.getPasswordHash());
		data.put(userID, true);
		return data;

	}

	// TODO: Add a salt to the hash, change to post request.
	/**
	 * Login request for the user. Hash of the user password is compared to the one
	 * stored in the HSM DB.
	 * 
	 * @param User ID, User Password
	 * @return boolean of the acceptance status of a login request.
	 */
	@CrossOrigin
	@GetMapping("/loginUser") // TODO refactor to hash on clientside before passing over web, investigate
								// certs
	@ResponseBody
	public Map<String, String> loginUser(@RequestParam String userID, @RequestParam String password)
			throws NoSuchAlgorithmException {
		HashMap<String, String> data = new HashMap<>();
		try {
			// Check if there is a user with the incoming ID, along with a password.
			if (service.getUserByUsername(userID) == null
					|| service.getUserByUsername(userID).getPasswordHash() == null) {
				data.put("Response", 401 + "");
				return data;
			}

			// Hash the incoming password, and check to see it matches the hash in the DB.
			String incomingHash = this.hash(password);
			if ((service.getUserByUsername(userID).getPasswordHash() + "").equals(incomingHash + "")) {
				data.put("Response", 200 + "");
				username = userID;
				return data;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Login failed");
			data.put("Response", 401 + "");
			return data;
		}
		return data;
	}

	/**
	 * Generate a pair of private and public keys using RSA.
	 *
	 * A key pair is generated using RSA, a key id is used to link this key to the
	 * user id. Private key is stored AES256 encrypted in the HSM DB. Key encryption
	 * key is calculated as follows: KEK = HSM Secret Key XOR SHA256(Key Password).
	 *
	 * @param Key userID.
	 * @throws Exception
	 * @returns Key ID, Public Key.
	 */
	@CrossOrigin
	@GetMapping("/generateKeyPair")
	@ResponseBody
	public Map<String, String> generateKeys(@RequestParam String keyPassword) throws Exception {
		HashMap<String, String> data = new HashMap<>();
		Base64.Encoder encoder = Base64.getEncoder();

		System.out.println("key Password:" + keyPassword);
		// Generate a key.
		try {
			KeyPair kp = _generateKeyPair(keyPassword);
			PublicKey pub = kp.getPublic();
			PrivateKey pvt = kp.getPrivate();

			// Public key.
			String pubKey_64 = encoder.encodeToString(pub.getEncoded());
			data.put("Key", pubKey_64);

			// Private Key.
			String privKey_64 = encoder.encodeToString(pvt.getEncoded());

			// Associate user with a key, and persist to database.
			String keyID = calcKeyID(keyPassword);
			service.createKey(username, keyID, privKey_64);
		} catch (Exception e) {
			data.put("Response", 500 + "");
		}
		return data;
	}

	private static KeyPair _generateKeyPair(String tweak) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom(tweak.getBytes()));
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}

	private String calcKeyID(String keypass) {
		String passHash = null;
		try {
			passHash = hash(keypass);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		String keyID = username + " " + passHash;
		return keyID;
	}

	// String keyID = calcKeyID( WHO_AM_I, keypass );

	// /**
	// * Basic architecture of sending and receiving data.
	// */
	// @CrossOrigin
	// @GetMapping("/test")
	// @ResponseBody
	// public Map<String, String> getFoos(@RequestParam String id) {
	// System.out.println(id);

	// HashMap<String, String> data = new HashMap<>();

	// // DO SOME COOL CRYPTO
	// int test = 500;
	// test = test % 9 * 4;

	// // RETURN IT
	// data.put("key", id + " and this is from spring");
	// data.put("data", test + "");
	// return data;
	// }
}
