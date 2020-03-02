package springboot;

import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

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
	private static final String KVC_PASSPHRASE = "test";
	static String username = "";
	@Autowired
	private PersistenceService service;

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

	private String _hash(String input) throws NoSuchAlgorithmException {
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

	/**
	 * Locates the private key corresponding to the provided Key ID. Returns the
	 * encryption of the provided text.
	 * 
	 * @param Text, Key ID, Key Password
	 * @return RSA(Text, Private Key from HSM DB)
	 */
	@CrossOrigin
	@GetMapping("/encrypt")
	@ResponseBody
	public Map<String, String> encrypt(@RequestParam String text, @RequestParam String eKeyID,
			@RequestParam String keyPassword) throws NoSuchAlgorithmException {
		HashMap<String, String> data = new HashMap<>();

		System.out.println(text);
		System.out.println(eKeyID);
		System.out.println(keyPassword);

		System.out.println(service.getKeyValueById(eKeyID));

		String prtKey = service.getKeyValueById(eKeyID);
		// Key privateKey = new Key();
		// privateKey.setValue(service.getKeyValueById(eKeyID));
//
	//	PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodedKey));

	//	String encryptedText = encrypt_RSA(text, Key);

		data.put("Key", "Yes");
		return data;
	}

	public static String encrypt_RSA(String plainText, PrivateKey privateKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);

		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

		return Base64.getEncoder().encodeToString(cipherText);
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

		for (int i = 0; i < users.size(); i++) {
			String addedKey = "";
			aUser = (User) users.get(i);
			keys = aUser.getKeys();
			if (keys != null && !keys.isEmpty()) {
				addedKey = aUser.getKeys().toString();
			}
			reportData.put(aUser + "", addedKey);
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
		String hash = this._hash(password);
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
	@GetMapping("/loginUser") // TODO refactor to hash on clientside before passing over web, certs
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
			String incomingHash = this._hash(password);
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
		if (service.getMasterKey() == null) {
			String hashForMasterKey = _hash("masterKeyPassword"); // TODO put in own method
			service.setMasterKey(hashForMasterKey);
		}
		// Initialize return map and base64 encoder
		HashMap<String, String> data = new HashMap<>();
		Base64.Encoder encoder = Base64.getEncoder();

		// Generate a key pair.
		try {
			KeyPair kp = _generateKeyPair(keyPassword);
			PublicKey pub = kp.getPublic();
			PrivateKey pvt = kp.getPrivate();

			// Public key.
			String pubKey_64 = encoder.encodeToString(pub.getEncoded());
			data.put("key", pubKey_64);

			// Private Key.
			String privKey_64 = encoder.encodeToString(pvt.getEncoded());

			// Generate a keyID.
			String keyID = calcKeyID(keyPassword);
			data.put("keyID", keyID);

			// Encrypt with AES256 the private key

			// Associate user with a key, and persist to database.
			service.createKey(username, keyID, privKey_64);

			// Calculate Key Encryption Key (KEK)
			// KEK = (HSMSecretKey) XOR (SHA256(KeyPassword))
			String keyEncryptionKey = "";
			try {
				String sha256KeyPass = _hash(keyPassword);
				keyEncryptionKey = _xorHex(service.getMasterKey().getValue() + "", sha256KeyPass);
				String pvtKey_encrypted = encrypt_AES(privKey_64, keyEncryptionKey);
				service.createKey(username, keyID, pvtKey_encrypted);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			data.put(keyID, pubKey_64); // still need to store this in the user, send it to them on reg

			// Calculate Key Verification Code (KVC)
			String plaintext = KVC_PASSPHRASE;
			String kekVerificationCode = encrypt_AES(plaintext, keyEncryptionKey); // store this, use it.

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
			passHash = _hash(keypass);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		String keyID = username + "-" + passHash.substring(0, 7); // questionable... user needs to be logged in
		return keyID;
	}

	private static String _xorHex(String a, String b) {
		char[] chars = new char[a.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = _toHex(_fromHex(a.charAt(i)) ^ _fromHex(b.charAt(i)));
		}
		return new String(chars);
	}

	private static int _fromHex(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		}
		if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		}
		throw new IllegalArgumentException();
	}

	private static char _toHex(int nybble) {
		if (nybble < 0 || nybble > 15) {
			throw new IllegalArgumentException();
		}
		char retChar = "0123456789abcdef".charAt(nybble);
		return retChar;
	}

	/** REFACTOR THIS WITHOUT STATIC */
	public static String encrypt_AES(String plaintext, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Encryption error: " + e.toString());
		}
		return null;
	}

	private static SecretKeySpec secretKey; // Used by AES encrypt/decrypt
	private static byte[] key;

	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static boolean confirmKVC(String kvcToConfirm, String tgtval, String kek) {
		boolean retbool = true;
		String decryption = decrypt_AES(kvcToConfirm, kek);
		retbool = decryption.equals(tgtval);
		return retbool;
	}

	public static String decrypt_AES(String ciphertext, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String retString = new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
			return retString;
		} catch (Exception e) {
			System.out.println("Decryption error: " + e.toString());
		}
		return null;
	}

	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(UTF_8));

		byte[] signature = privateSignature.sign();

		return Base64.getEncoder().encodeToString(signature);
	} // Closing sign()

	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);

		return publicSignature.verify(signatureBytes);
	}
	/** END OF REFACTOR */

}
