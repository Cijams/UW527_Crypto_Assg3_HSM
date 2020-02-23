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
    return "Hash Called - DEPRECATED, USE SHA256() or passwordHash()";
  }

  @RequestMapping("/encrypt")
  public String encrypt() {
    return "Encryption Called - DEPRECATED, USE HSMEncrypt( String, String, String )";
  }

  @RequestMapping("/login")
  public String login() {
    return "Login Called - DEPRECATED, USE loginUser( String, String )";
  }

  @RequestMapping("/register")
  public String register() {
    return "Register Called - DEPRECATED, USE registerUser( String, String )";
  }

  @RequestMapping("/genKeys")
  public String genKeys() {
    return "genKeys() called - DEPRECATED, USE createKey( String, boolean, String )"
  }

  @RequestMapping("/getKeys")
  public String getKeys() {
    return "GetKeys Called - DEPRECATED, USE keyLookup( String, String )";
  }

  @RequestMapping("/decrypt")
  public String decrypt() {
    return "Decrypt Called - DEPRECATED, USE <?>";
  }

  @RequestMapping("/sign")
  public String sign() {
    return "Sign Called - DEPRECATED, USE <?>";
  }

  @RequestMapping("/XOR")
  public static String XOR( String key, String target ) {
    String result = "<TODO: XOR the key and target together>";
    return result;
  }
  
  @RequestMapping("/SHA256")
  public static String SHA256( String input ) {
    String hash = "<TODO: HASH_THE_INPUT>";
    return hash;
  }
  
  @RequestMapping("/RSAPrivate")
  public static String RSAPrivate( String keyPass ) {
    String retString = "<TODO: RSA_PRIVATE_KEY>";
    return retString;
  }
​
  @RequestMapping("/RSAPublic")
  public static String RSAPublic( String keyPass ) {
    String retString = "<TODO: RSA_PUBLIC_KEY>";
    return retString;
  }
  
  @RequestMapping("/HSMIdentify")
  public static String HSMIdentify( String keyPass ) {
    String retString = "<TODO: HSM_KEY_IDENTIFIER>";
    return retString;
  }
​
  @RequestMapping("/AES256encrypt")
  public static String AES256encrypt( String plaintext ) {
    String ciphertext = "<TODO: Encrypt the plaintext>";
    return ciphertext;
  }
  
  @RequestMapping("/passwordHash")  
  public static String passwordHash( String pass ) {
    String retString = "FOOBARBAZ";
    // TODO: Hash the password into a string
    return retString;
  }
  
  @RequestMapping("/databaseWrite")
  public static boolean databaseWrite( String name, String hash ) {
    boolean result = true; // TODO: Database call
    return result;
  }

  @RequestMapping("/databaseRead")  
  public static boolean databaseRead( String name, String hash ) {
    boolean result = true; // TODO: Database call
    return result;
  }
  
  // Looks up a name:keyID pair
  // True  - If pair is found
  // False - If pair is not found
  @RequestMapping("/HSMDatabaseRead")
  public static boolean HSMDatabaseRead( String name, String keyID ) {
    boolean result = false; // TODO: Database call
    return result;
  }
  
  @RequestMapping("/loginUser")
  public static boolean loginUser( String name, String pass ) {
    String hash = passwordHash( pass );
    // Attempt to find combination in Database
    if( databaseRead( name, hash ) ) {
      if( DEBUG ) {
        System.out.println( "User found! Logging in..." );
      }
      return true;
    }
    else {
      if( DEBUG ) {
        System.out.println( "User/password not found. Access denied." );
      }
      return false;
    }
  }

  @RequestMapping("/keyLookup")
  public static boolean keyLookup( String name, String keyID ) {
    // TODO: Database call
    boolean result = false; // False if not found
    return result;
  }
  
  @RequestMapping("/registerUser")
  public static boolean registerUser( String name, String pass ) {
    if( DEBUG ) {
      System.out.println( "Received arguments: " );
      System.out.println( "  NAME: " + name );
      System.out.println( "  PASS: " + pass );
    }
    
    String hash = passwordHash( pass );
    
    if( DEBUG ) {
      System.out.println( "Arguments sending to database:" );
      System.out.println( "  NAME: " + name );
      System.out.println( "  HASH: " + hash );
    }
    
    if( databaseRead( name, hash ) ) {
      if( DEBUG ) {
        System.out.println( "Entry found in database. Aborting registration..." );
      }
      return false;
    }
    else {
      if( DEBUG ) {
        System.out.println( "Entry not found in database. Attempting to write..." );  
      }
    }
    
    // Attempt to enter name and hash to database
    boolean writeResult = databaseWrite( name, hash );
    return writeResult;
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
