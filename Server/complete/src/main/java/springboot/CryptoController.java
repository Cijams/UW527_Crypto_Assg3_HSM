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
		System.out.println(userID);
		System.out.println(service.getKeyIdsByUsername(userID));
		returnKeys.put("User ID keys", service.getKeyIdsByUsername(userID));
		return returnKeys;
	}

	/**
	 * Creates a digital signature based on user supplied input.
	 * 
	 * @param textToSign
	 * @param eKeyID
	 * @param keyPassword
	 * @return
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

		System.out.println(textToSign);
		System.out.println(eKeyID);
		System.out.println(keyPassword);
		String sha256KeyPass = _hash(keyPassword);

		String keyEncryptionKey = _xorHex(service.getMasterKey().getValue() + "", sha256KeyPass);

		String pvtKey_encrypted = service.getKeyValueById(eKeyID);

		String unencryptedPrivateKey = decrypt_AES(pvtKey_encrypted, keyEncryptionKey);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decoder.decode(unencryptedPrivateKey));
		PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

		byte[] sha2 = new byte[0];
		// StringBuilder hexString = null;
		sha2 = generateSha2(textToSign);

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey2);
		byte[] digitalSignature = cipher.doFinal(sha2);

		String rds = encoder.encodeToString(digitalSignature);

		hashedData.put("Signature", rds);
		return hashedData;
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
	 * @throws Exception
	 */
	@CrossOrigin
	@GetMapping("/decrypt")
	@ResponseBody
	public Map<String, String> decrypt(@RequestParam String keyPassword, @RequestParam String cipherText,
			@RequestParam String eKeyID) throws Exception {
		HashMap<String, String> data = new HashMap<>();
		Base64.Decoder decoder = Base64.getDecoder();

		String key = service.getPublicKeyValueById(eKeyID);
		System.out.println(key);

		byte[] pubKeySeed = Base64.getDecoder().decode(key);
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKeySeed));

		// byte[] dataBytes = Base64.getMimeDecoder().decode(key);

		// X509EncodedKeySpec spec = new
		// X509EncodedKeySpec(Base64.getDecoder().decode(dataBytes));

		// byte[] keyBytes = key.getBytes();
		// byte[] dataBytes = Base64.getMimeDecoder().decode(key);

		// X509EncodedKeySpec spec =
		// new X509EncodedKeySpec(dataBytes);
		// KeyFactory kf = KeyFactory.getInstance("RSA");
		// System.out.println(kf.generatePublic(spec));
		// PublicKey pki = kf.generatePublic(spec);

		// System.out.println();
		// System.out.println("____");
		// System.out.println(spec);
		// System.out.println("____");
		// System.out.println();

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
	 * @param plainText The text to be encrypted.
	 * @param privateKey The private RSA key used to encrypt.
	 * @return	The ciphertext result of RSA encryption.
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
	 * @param publicKey The public RSA key used to decrypt.
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
	 */
	@CrossOrigin
	@GetMapping("/generateReport")
	public Map<String, String> genReport() {
		LinkedHashMap<String, String> reportData = new LinkedHashMap<>();

		List users = null;
		List keys = null;
		User aUser = null;
		int length = 0;

		users = service.findAllUsers();

		System.out.println("TEST TEST 2");
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

		System.out.println("TEST TREE");
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
		if (service.getUserByUsername(userID) != null || password.length() < 1) {
			data.put(userID, false);
			return data;
		}
		try {
			System.out.println("User name:" + userID + "\nPassword: " + password);
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

	// @CrossOrigin
	// @GetMapping("/sign")
	// @ResponseBody
	// public Map<String, String> sign(@RequestParam String text, @RequestParam
	// String eKeyID,
	// @RequestParam String keyPassword) throws Exception {
	// HashMap<String, String> data = new HashMap<>();

	// String pvtKey = service.getKeyValueById(eKeyID);
	// byte[] decodedKey = Base64.getDecoder().decode(pvtKey);
	// PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new
	// PKCS8EncodedKeySpec(decodedKey));

	// String signature = sign(text, privateKey);

	// data.put("Signature", signature);
	// return data;
	// }

	@CrossOrigin
	@GetMapping("/verify")
	@ResponseBody
	public Map<String, Boolean> verify(@RequestParam String text, @RequestParam String eKeyID,
			@RequestParam String keyPassword) throws Exception {
		HashMap<String, Boolean> data = new HashMap<>();

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
	@GetMapping("/loginUser") // TODO refactor to hash on clientside before passing over web, certs
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
		// once per instance.
		// can use KEK to secure the master key with a password.
		_ensureMasterKeyEstablished();

		// Initialize return map and base64 encoder
		HashMap<String, String> data = new HashMap<>();
		Base64.Encoder encoder = Base64.getEncoder();
		Base64.Decoder decoder = Base64.getDecoder();

		if (keyPassword.length() < 1) {
			data.put("False", "Enter a password for key generation.");
			return data;
		}

		if (_validateUser(userID)) {
			// Generate a key pair.
			try {
				KeyPair kp = _generateKeyPair(keyPassword);
				PublicKey pub = kp.getPublic();
				PrivateKey pvt = kp.getPrivate();

				TEMPPUBKEY = pub;

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
				// KEK = (HSMSecretKey) XOR (SHA256(KeyPassword))
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
				data.put(keyID, pubKey_64); // still need to store this in the user, send it to them on reg

				pvtKey_encrypted = service.getKeyValueById(keyID);

				String unencryptedPrivateKey = decrypt_AES(pvtKey_encrypted, keyEncryptionKey);

				KeyFactory keyFactory = KeyFactory.getInstance("RSA");

				EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decoder.decode(unencryptedPrivateKey));
				PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

				// KVC Generation.

				System.out.println(pubKey_64);
				EncodedKeySpec publicKeySpec = new PKCS8EncodedKeySpec(decoder.decode(pubKey_64));
			} catch (Exception e) {
				e.printStackTrace();
				data.put("Response", 500 + "");
			}
		}
		return data;
	}

	private static KeyPair _generateKeyPair(String tweak) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom(tweak.getBytes()));
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}

	private String calcKeyID(String keypass, String userID) {
		String passHash = null;
		try {
			passHash = _hash(keypass);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		String keyID = userID + "-" + passHash.substring(0, 7); // questionable... user needs to be logged in
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
