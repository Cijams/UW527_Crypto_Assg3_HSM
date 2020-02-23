package com.example.springboot;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class CryptoController {

  public static final boolean DEBUG = true;
  public static final String HSM_SECRET_KEY = "This_Is_The_HSM_Secret_Key"; // Simulated secret stored in the "hardware"

  public static void main(String[] args) {
      
    boolean result = registerUser( "MyName", "MyPassword" );
    System.out.println( "Result of registerUser(): " + result );
    
    result = loginUser( "MyName", "MyPassword" );
    System.out.println( "Result of loginUser(): " + result );
    
    boolean loggedIn = result;
    
    String username = "MyName";
    String keyPassword = "Quack!Quack!Quack!";
    
    result = createKey( username, loggedIn, keyPassword);
    System.out.println( "Result of createKey(): " + result );
    
    String plaintext = "This is a test of the HSM encryption module.";
    String keyID = "<TODO: Accept a keyID from the user>";
    keyPassword = "<TODO: Accept a keyPassword from the user>";
    String ciphertext = HSMEncrypt( plaintext, keyID, keyPassword );
    System.out.println( "Result of HSMEncrypt(): " + ciphertext );
    
    HSMReport();
  }

  @RequestMapping("/")
  public String index() {
    return "Server Up";
  }

  @RequestMapping("/hash")
  public String hash() {
    return "Hash Called";
  }

  @RequestMapping("/encrypt")
  public String encrypt() {
    return "Encryption Called - DEPRECATED, USE HSMEncrypt( String, String, String )";
  }

  @RequestMapping("/login")
  public String login() {
    return "Login Called";
  }

  @RequestMapping("/register")
  public String register() {
    return "Register Called";
  }

  @RequestMapping("/genKeys")
  public String genKeys() {
    return "genKeys() called - DEPRECATED, USE createKey( String, boolean, String )"
  }

  @RequestMapping("/getKeys")
  public String getKeys() {
    return "GetKeys Called";
  }

  @RequestMapping("/decrypt")
  public String decrypt() {
    return "Decrypt Called";
  }

  @RequestMapping("/sign")
  public String sign() {
    return "Sign Called";
  }

  @RequestMapping("/HSMEncrypt")
  public static String HSMEncrypt( String plaintext, String keyID, String keyPassword ) {
    // TODO: Find the encrypted key from the HSM database using the keyID
    // TODO: Decrypt the retrieved key using the keyPassword
    String retString = "<TODO: RSA encrypt the plaintext with the decrypted HSM database key>";
    return retString;
  }

  @RequestMapping("/createKey")
  public static boolean createKey( String name, boolean loggedIn, String keyPassword ) {
    if( DEBUG ) {
      System.out.println( "createKey called:" );
      System.out.println( "  Name    : " + name );
      System.out.println( "  LoggedIn: " + loggedIn );
      System.out.println( "  KeyPass : " + keyPassword );
    }
    
    if( !loggedIn ) {
      if( DEBUG ) {
        System.out.println( "User not logged in. Aborting key generation..." );
      }
      return false;
    }
    
    String privateKey    = RSAPrivate(  keyPassword );
    String publicKey     = RSAPublic(   keyPassword );
    String keyID         = HSMIdentify( keyPassword );
    String SHA256ofPass  = SHA256(      keyPassword );
​
    // TODO: Where do we send all these pieces...?
    
    if( DEBUG ) {
      System.out.println( "RSA Encryption Complete:" );
      System.out.println( "  RSA Private Key: " + privateKey   );
      System.out.println( "  RSA Public Key : " + publicKey    );
      System.out.println( "  HSM Key ID     : " + keyID        );
      System.out.println( "  SHA256 of Pass : " + SHA256ofPass );
    }
    
    if( keyLookup( name, keyID ) ) {
      if( DEBUG ) {
        System.out.println( "Key already found in HSM database. Aborting key generation..." );
      }
      return false;
    }
​
    // Otherwise we're logged in and the key doesn't already exist, so save it
​
    String privateKeyCipher = AES256encrypt( privateKey );
    // TODO: Send privateKeyCipher to HSM database
    
    // TODO: Something about a KEK here
    // KEK = HSM Secret Key XOR SHA256(Key Password)
    String KeyEncryptKey = XOR( HSM_SECRET_KEY, SHA256ofPass );
​
    if( DEBUG ) {
      System.out.println( "Key Encrypted Key (KEK) generated:" );
      System.out.println( "  KEK: " + KeyEncryptKey );
    }
    
    // OUTPUT:
    System.out.println( "Key ID    : " + keyID );
    System.out.println( "Public Key: " + publicKey );
​
    return true; // If successful
  }

  public static void HSMReport() {
    System.out.println( "Backdoor method: Report the contents of the HSM database:" );
    System.out.println( "  Key 1: " );
    System.out.println( "  Key 2: " );
    System.out.println( "  Key 3: etc etc etc" );
  }

}
