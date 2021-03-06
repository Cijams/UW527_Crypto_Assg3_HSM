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
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
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
	private static final String MASTERPASSWORD = "masterKeyPassword";
	PublicKey TEMPPUBKEY;

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
	 * Generates a SHA-256 hash value.
	 */
	@CrossOrigin
	@GetMapping("/hash")
	public Map<String, String> hashWeb(@RequestParam String textToHash) throws NoSuchAlgorithmException {
		HashMap<String, String> hashedData = new HashMap<>();
		byte[] sha2 = new byte[0];
		StringBuilder hexString = null;
		sha2 = generateSha2(textToHash);
		// Convert SHA-2 to hexadecimal
		hexString = _generateHex(sha2);
		hashedData.put("Hash:", hexString.toString());
		return hashedData;
	}

	/**
	 * Returns the public keys associated with a user.
	 * 
	 * @param eKeyID The key ID used for public key lookup.
	 * @return A public key.
	 * @throws NoSuchAlgorithmException
	 */
	@CrossOrigin
	@GetMapping("/getPubKeys")
	@ResponseBody
	public Map<String, String> getPubKeys(@RequestParam String eKeyID) throws NoSuchAlgorithmException {
		HashMap<String, String> returnKey = new HashMap<>();
		String keyLookup = service.getPublicKeyValueById(eKeyID);
		returnKey.put("Public key", keyLookup);
		return returnKey;
	}

	/**
	 * Returns the key ID's associated with a user.
	 * 
	 * @param userID The userID for lookup.
	 * @return The associated key ID's.
	 * @throws NoSuchAlgorithmException
	 */
	@CrossOrigin
	@GetMapping("/getKeyIDs")
	@ResponseBody
	public Map<String, List<String>> getKeyIDs(@RequestParam String userID) throws NoSuchAlgorithmException {
		HashMap<String, List<String>> returnKeys = new HashMap<>();
		returnKeys.put("User ID keys", service.getKeyIdsByUsername(userID));
		return returnKeys;
	}

	/**
	 * Creates a digital signature based on user supplied input.
	 * 
	 * @param textToSign  The incoming string that will be signed.
	 * @param eKeyID      The key lookup for signing.
	 * @param keyPassword The key password for signing.
	 * @return A hash signature.
	 * @throws Exception
	 */
	@CrossOrigin
	@GetMapping("/sign")
	@ResponseBody
	public Map<String, String> digitalSignature(@RequestParam String textToSign, @RequestParam String eKeyID,
			@RequestParam String keyPassword) throws Exception {
		HashMap<String, String> hashedData = new HashMap<>();
		Base64.Decoder decoder = Base64.getDecoder();
		Base64.Encoder encoder = Base64.getEncoder();

		// Regenerate the SHA256 key and key encryption key.
		String sha256KeyPass = _hash(keyPassword);
		String keyEncryptionKey = _xorHex(service.getMasterKey().getValue() + "", sha256KeyPass);

		// Regenerate the encrypted RSA private key from the vHSM database using user
		// credentials.
		String pvtKey_encrypted = service.getKeyValueById(eKeyID);
		String unencryptedPrivateKey = decrypt_AES(pvtKey_encrypted, keyEncryptionKey);

		// Convert strings to actual key object.
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decoder.decode(unencryptedPrivateKey));
		PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

		// Digital Signature
		byte[] sha2 = new byte[0];
		sha2 = generateSha2(textToSign);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey2);
		byte[] digitalSignature = cipher.doFinal(sha2);

		String rds = encoder.encodeToString(digitalSignature);

		hashedData.put("Signature", rds);
		return hashedData;
	}

	/**
	 * Auxiliary method used to apply SHA256 algorithm to incoming text.
	 * 
	 * @param input A string of text.
	 * @return A SHA256 Hash of the text.
	 * @throws NoSuchAlgorithmException
	 */
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
	 * @throws Exception
	 */
	@CrossOrigin
	@GetMapping("/decrypt")
	@ResponseBody
	public Map<String, String> decrypt(@RequestParam String keyPassword, @RequestParam String cipherText,
			@RequestParam String eKeyID) throws Exception {
		HashMap<String, String> data = new HashMap<>();

		// Regenerate the public RSA key.
		String key = service.getPublicKeyValueById(eKeyID);
		byte[] pubKeySeed = Base64.getDecoder().decode(key);
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKeySeed));

		// Decrypt the ciphertext.
		String decryptedText = decrypt_RSA(cipherText, publicKey);

		data.put("Decrypted:", decryptedText);
		return data;
	}

	/**
	 * Locates the private key corresponding to the provided Key ID. Returns the
	 * encryption of the provided text.
	 * 
	 * @param Text, Key ID, Key Password
	 * @return RSA(Text, Private Key from HSM DB)
	 * @throws Exception
	 */
	@CrossOrigin
	@GetMapping("/encrypt")
	@ResponseBody
	public Map<String, String> encrypt(@RequestParam String text, @RequestParam String eKeyID,
			@RequestParam String keyPassword) throws Exception {
		HashMap<String, String> data = new HashMap<>();
		Base64.Decoder decoder = Base64.getDecoder();

		try {
			// Regenerate the SHA256 key and key encryption key.
			String sha256KeyPass = _hash(keyPassword);
			String keyEncryptionKey = _xorHex(service.getMasterKey().getValue() + "", sha256KeyPass);

			// Generate the key verification code and ensure the correct KEK.
			String kvc = service.getKvcById(eKeyID);
			boolean kvcResult = confirmKVC(kvc, KVC_PASSPHRASE, keyEncryptionKey);
			if (kvcResult != true) {
				data.put("KVC FAILURE", "Key Verification Code comparison failed.");
				return data;
			}

			// Regenerate the encrypted RSA private key from the vHSM database using user
			// credentials.
			String pvtKey_encrypted = service.getKeyValueById(eKeyID);
			String unencryptedPrivateKey = decrypt_AES(pvtKey_encrypted, keyEncryptionKey);
			String encrytpedText = "";
			try {
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decoder.decode(unencryptedPrivateKey));
				PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

				// Encrypt the incoming plaintext.
				encrytpedText = encrypt_RSA(text, privateKey2);
			} catch (Exception e) {
				data.put("False", "Unable to encrypt data. Password and key ID do not match.");
				return data;
			}
			data.put("Encrypted:", encrytpedText);
			return data;
		} catch (Exception e) {
			data.put("Failer", "Failure to encrypt.");
			return data;
		}
	}

	/**
	 * Use the RSA algorithm to encrypt the incoming plaintext.
	 * 
	 * @param plainText  The text to be encrypted.
	 * @param privateKey The private RSA key used to encrypt.
	 * @return The ciphertext result of RSA encryption.
	 * @throws Exception
	 */
	public static String encrypt_RSA(String plainText, PrivateKey privateKey) throws Exception {
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));

		return Base64.getEncoder().encodeToString(cipherText);
	}

	/**
	 * Use the RSA algorithm to decrypt the incoming ciphertext.
	 * 
	 * @param cipherText The text to be decrypted.
	 * @param publicKey  The public RSA key used to decrypt.
	 * @return The plaintext result of RSA decryption.
	 * @throws Exception
	 */
	public static String decrypt_RSA(String cipherText, PublicKey publicKey) throws Exception {
		cipherText = cipherText.replace(" ", "+");
		byte[] bytes = Base64.getDecoder().decode(cipherText);
		Cipher decriptCipher = Cipher.getInstance("RSA");
		decriptCipher.init(Cipher.DECRYPT_MODE, publicKey);
		return new String(decriptCipher.doFinal(bytes), UTF_8);
	}

	/**
	 * Generates a report summarizing the status of the HSM. A list of the
	 * registered users and their associated stored keys.
	 * 
	 * @return A list of all user-key data.
	 */
	@CrossOrigin
	@GetMapping("/generateReport")
	public Map<String, String> genReport() {

		// Temportary data storage.
		LinkedHashMap<String, String> reportData = new LinkedHashMap<>();
		List<User> users = null;
		List<Key> keys = null;
		User aUser = null;

		// Collect all users and their associated keys.
		users = service.findAllUsers();
		for (int i = 0; i < users.size(); i++) {
			String addedKey = "";
			aUser = (User) users.get(i);
			keys = aUser.getKeys();
			if (keys != null && !keys.isEmpty()) {
				addedKey = aUser.getKeys().toString();
			}
			reportData.put(("User: " + aUser + ""),
					(addedKey.substring(0, addedKey.length() >= 20 ? 20 : addedKey.length()) + " . . . "));
		}

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

		// Ensure there is a user name and password.
		if (service.getUserByUsername(userID) != null || password.length() < 1) {
			data.put(userID, false);
			return data;
		}

		// Create the user with a password.
		try {
			User user = new User();
			user.setUserName(userID);
			String hash = this._hash(password);
			user.setPasswordHash(hash);
			service.createUser(user.getUserName(), user.getPasswordHash());
			data.put(userID, true);
			return data;
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
			data.put("Unable to login", false);
			return data;
		}
	}

	@CrossOrigin
	@GetMapping("/verify")
	@ResponseBody
	public Map<String, Boolean> verify(@RequestParam String text, @RequestParam String eKeyID,
			@RequestParam String keyPassword) throws Exception {
		HashMap<String, Boolean> data = new HashMap<>();

		// Ensure the digital signature is correct.
		String pvtKey = service.getKeyValueById(eKeyID);
		byte[] decodedKey = Base64.getDecoder().decode(pvtKey);
		PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
		String signature = sign(text, privateKey);

		boolean isCorrect = verify(text, signature, TEMPPUBKEY);

		data.put("data is:", isCorrect);
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
	@GetMapping("/loginUser")
	@ResponseBody
	public Map<String, String> loginUser(@RequestParam String userID, @RequestParam String password)
			throws NoSuchAlgorithmException {
		HashMap<String, String> data = new HashMap<>();
		try {
			service.getKeyIdsByUsername(userID).toString();

			// Ensure valid user credentials are being sent.
			boolean validUserCredentials = this._validateUserCredentials(userID, password);
			if (validUserCredentials == true) {
				data.put(userID, validUserCredentials + "");
				return data;
			} else {
				data.put("Login Status", false + "");
				return data;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Login failed");

			data.put("Login Status", false + "");
			return data;
		}
	}

	/**
	 * Check if there is a user with the incoming ID, along with a password. Hash
	 * the incoming password, and check to see it matches the hash in the DB.
	 * 
	 * @param User ID, User Password
	 * @return boolean of the acceptance status of the user credentials.
	 */
	private boolean _validateUserCredentials(String userID, String userPassword) throws NoSuchAlgorithmException {
		boolean credsAreValid = false;
		if (service.getUserByUsername(userID) != null && service.getUserByUsername(userID).getPasswordHash() != null) {
			String incomingHash = this._hash(userPassword);
			if ((service.getUserByUsername(userID).getPasswordHash() + "").equals(incomingHash + "")) {
				credsAreValid = true;
			}
		}
		return credsAreValid;
	}

	/**
	 * Check if there is a user with the incoming ID, along with a password. Hash
	 * the incoming password, and check to see it matches the hash in the DB.
	 * 
	 * @param User ID, User Password
	 * @return boolean of the acceptance status of the user credentials.
	 */
	private boolean _validateUser(String userID) throws NoSuchAlgorithmException {
		boolean credsAreValid = false;
		if (service.getUserByUsername(userID) != null && service.getUserByUsername(userID).getPasswordHash() != null) {
			credsAreValid = true;
		}
		return credsAreValid;
	}

	/**
	 * Ensures that the vHSM has a master password associated with its data storage.
	 * 
	 * @return Status of the existence of a master password.
	 * @throws NoSuchAlgorithmException
	 */
	private boolean _ensureMasterKeyEstablished() throws NoSuchAlgorithmException {
		boolean masterKeyStatus = false;
		if (service.getMasterKey() == null) {
			String hashForMasterKey = _hash(MASTERPASSWORD);
			service.setMasterKey(hashForMasterKey);
			masterKeyStatus = true;
		}
		return masterKeyStatus;
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
	public Map<String, String> generateKeys(@RequestParam String userID, @RequestParam String keyPassword)
			throws Exception {

		// Ensure the vHSM database has an active working secret key. Set secret key
		// once per instance. Use KEK to secure the master key with a password.
		_ensureMasterKeyEstablished();

		// Initialize return map and base64 encoder
		HashMap<String, String> data = new HashMap<>();
		Base64.Encoder encoder = Base64.getEncoder();

		// Ensure there exists an incoming password.
		if (keyPassword.length() < 1) {
			data.put("False", "Enter a password for key generation.");
			return data;
		}

		// Ensure valid user.
		if (_validateUser(userID)) {
			// Generate a key pair.
			try {
				KeyPair kp = _generateKeyPair(keyPassword);
				PublicKey pub = kp.getPublic();
				PrivateKey pvt = kp.getPrivate();

				// Public key.
				String pubKey_64 = encoder.encodeToString(pub.getEncoded());

				// Private Key.
				String privKey_64 = encoder.encodeToString(pvt.getEncoded());

				// Generate a keyID.
				String keyID = calcKeyID(keyPassword, userID);
				if (service.getKeyValueById(keyID) != null) {
					data.put("False", "A key with that password already exists.");
					return data;
				}

				// Encrypt with AES256 the private key
				// Calculate Key Encryption Key (KEK)
				// [ KEK = (HSMSecretKey) XOR (SHA256(KeyPassword)) ]
				String keyEncryptionKey = "";
				String pvtKey_encrypted = "";
				String sha256KeyPass = "";
				try {
					sha256KeyPass = _hash(keyPassword);
					keyEncryptionKey = _xorHex(service.getMasterKey().getValue() + "", sha256KeyPass);
					pvtKey_encrypted = encrypt_AES(privKey_64, keyEncryptionKey);
					String kvc = encrypt_AES(KVC_PASSPHRASE, keyEncryptionKey);
					service.createKey(userID, keyID, pvtKey_encrypted, pubKey_64, kvc);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
				data.put(keyID, pubKey_64);
			} catch (Exception e) {
				e.printStackTrace();
				data.put("Response", 500 + "");
			}
		}
		return data;
	}

	/**
	 * Generate an RSA private-public key pair.
	 * 
	 * @param tweak text used to make the generation unique.
	 * @return The RSA Keys.
	 * @throws Exception
	 */
	private static KeyPair _generateKeyPair(String tweak) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom(tweak.getBytes()));
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}

	/**
	 * Calculates a key identification used to associate user with RSA keyset.
	 * 
	 * @param keypass The user provided password.
	 * @param userID  The user identification.
	 * @return The key ID.
	 */
	private String calcKeyID(String keypass, String userID) {
		String passHash = null;
		try {
			passHash = _hash(keypass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String keyID = userID + "-" + passHash.substring(0, 7); // questionable... user needs to be logged in
		return keyID;
	}

	/**
	 * Auxiliary method to XOR hexidecimal text strings.
	 * 
	 * @param a Text.
	 * @param b Text.
	 * @return XOR operation on the text.
	 */
	private static String _xorHex(String a, String b) {
		char[] chars = new char[a.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = _toHex(_fromHex(a.charAt(i)) ^ _fromHex(b.charAt(i)));
		}
		return new String(chars);
	}

	/**
	 * Auxiliary method used to get data from hex.
	 * 
	 * @param c A Char.
	 * @return A number.
	 */
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

	/**
	 * Auxiliary method used to convert to hexidecimal.
	 * 
	 * @param nybble Incomding data.
	 * @return The hexified char.
	 */
	private static char _toHex(int nybble) {
		if (nybble < 0 || nybble > 15) {
			throw new IllegalArgumentException();
		}
		char retChar = "0123456789abcdef".charAt(nybble);
		return retChar;
	}

	/**
	 * Encrypt data use Advanced Encryption Standard.
	 * 
	 * @param plaintext The text string to be encrypted.
	 * @param secret    The password for encryption.
	 * @return The ciphertext of the AES encryption operation.
	 */
	public static String encrypt_AES(String plaintext, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Used by AES encrypt/decrypt
	private static SecretKeySpec secretKey;
	private static byte[] key;

	/**
	 * Sets a key for operations.
	 * 
	 * @param myKey The text of the Key.
	 */
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

	/**
	 * 
	 * @param kvcToConfirm The registered key verification code.
	 * @param tgtval       The target value to be tested.
	 * @param kek          The key encryption key.
	 * @return A boolean status on the KVC truthiness.
	 */
	public static boolean confirmKVC(String kvcToConfirm, String tgtval, String kek) {
		boolean retbool = true;
		String decryption = decrypt_AES(kvcToConfirm, kek);
		retbool = decryption.equals(tgtval);
		return retbool;
	}

	/**
	 * Decrypts cipher text using AES.
	 * 
	 * @param ciphertext The ciphertext to be decrypted.
	 * @param secret     The associated passcode.
	 * @return The plaintext form of the data.
	 */
	public static String decrypt_AES(String ciphertext, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String retString = new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
			return retString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Applied digital signature algorithm.
	 * 
	 * @param plainText  The incoming plaintext to be signed.
	 * @param privateKey The private key associated with the RSA algorithm.
	 * @return A digital signature.
	 * @throws Exception
	 */
	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
		Signature privateSignature = Signature.getInstance("SHA256withRSA");
		privateSignature.initSign(privateKey);
		privateSignature.update(plainText.getBytes(UTF_8));

		byte[] signature = privateSignature.sign();

		return Base64.getEncoder().encodeToString(signature);
	}

	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(publicKey);
		publicSignature.update(plainText.getBytes(UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);

		return publicSignature.verify(signatureBytes);
	}

}
