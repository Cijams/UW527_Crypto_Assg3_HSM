package springboot;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersistenceServiceImplTest {

	@Autowired
	private PersistenceService service;

	// test User objects
	private User testUser1;
	private User testUser2;
	
    @After
    public void cleanUp() throws Exception {
    	
    	// destroy test users
    	service.deleteUser( testUser1 );
    	service.deleteUser( testUser2 );

    	// reset master key
    	service.setMasterKey( "FEEDFACECAFEBEEF" );

    }

    @Test
    public void createDeleteKey() {
    	
    	String keyId = "OpenVMS";
    	String keyValue = "ForeverForever";
    	
    	// create the key
    	Key newKey = service.createKey( testUser2.getUserName(), keyId, keyValue );
    	String s = newKey.toString();
    	
    	// retrieve the test user again
    	User user = service.getUserByUsername( testUser2.getUserName() );
    	
    	// should exist
    	assertThat( newKey ).as( "New Key should not be NULL!" ).isNotNull();
    	assertThat( user.getKeys().contains( newKey ) ).as( "New Key should have been associated with test user" ).isTrue();
    	
    	// delete the Key
    	user = service.deleteKey( testUser2.getUserName(), keyId );
    	
    	// should no longer exist
    	assertThat( user.getKeys().contains( newKey ) ).as( "New Key should no longer be associated with test user" ).isFalse();
    	
    }
    
    @Test
    public void createUser() {
    	
    	String userId = "Pontiac";
    	String passwordHash = "WS6";
    	
    	// create the user
    	service.createUser( userId, passwordHash );
    	
    	// see if the user exists in the database
    	User newUser = service.getUserByUsername( userId );
    	assertThat( newUser ).as( "User must have been created using username and password! ").isNotNull();
    	
    	// remove cruft
    	service.deleteUser( newUser );
    	
    }
    
    @Test
    public void findAllUsers() {
        
        List< User > userList = service.findAllUsers();
        
        assertThat( userList.size() ).as( "Database should contain at least two users!" ).isGreaterThan( 1 );
        assertThat( userList.contains( testUser1 ) ).as( "Test User #1 should exist in the database!" ).isTrue();
        assertThat( userList.contains( testUser2 ) ).as( "Test User #2 should exist in the database!" ).isTrue();
        
    }
	
    @Test
    public void findUserByUserName() {
    	
    	assertThat( service.getUserByUsername( testUser1.getUserName() ) ).as( "Unable to find test user #1 using username!" ).isNotNull();
    	
    }
    
    @Test
	public void getCount() {

		// get the current value of a counter
		long countValue = service.getCount( User.SEQUENCE_NAME ); 
		
		// next call should be incremented
		long nextCountValue = service.getCount( User.SEQUENCE_NAME );

		// remove cruft from this test
		service.deleteSequencer( User.SEQUENCE_NAME );

		assertThat( countValue ).as( "Sequencer should start at zero!" ).isZero();
		assertThat( nextCountValue ).as( "Sequencer should auto-increment!" ).isOne();
		
	}
    
    @Test
	public void getKeyValueById() {
		
    	String keyId = "Jaguar";
    	String keyValue = "XJ6";
    	
    	// create a test key
    	Key newKey = service.createKey( testUser1.getUserName(), keyId, keyValue );

		// try getting the value of a key we know exists
		String value = service.getKeyValueById( keyId );
		assertThat( value ).as( "Should be able to find Key by ID!" ).isEqualTo( keyValue );

		// try getting the value of a non-existent Key
		value = service.getKeyValueById( "HolyGrail" );
		assertThat( value ).as( "Should get a NULL return when requesting value of a key that doesn't exist!" ).isNull();

	}
    
    @Test
    public void masterKeyPersist() {
    	
    	String masterKeyValue = "AE559426858CA3CF50F32D7B3F881560B6C3B4FA32AC265DDBBD7ABC6FECD5CA";
    	
    	// set the master key first
    	service.setMasterKey( masterKeyValue );
    	
    	// retrieve the master key and verify value set
    	Key masterKey = service.getMasterKey();
    	assertThat( masterKey.getValue() ).as( "Master key value should be set properly!" ).isEqualTo( masterKeyValue );
    	
    }
    
	@Before
    public void setUp() throws Exception {
        
    	// create test users
    	testUser1 = new User();
    	testUser1.setUserName( "Smeghead" );
    	testUser1 = service.createUser( testUser1 );

    	testUser2 = new User();
    	testUser2.setUserName( "dlister" );
    	testUser2 = service.createUser( testUser2 );

    }
    
	@Test
    public void updateUser() {
    	
    	String newFieldValue = "DEADBEEF";
    	
    	// change a User field
    	testUser1.setPasswordHash( newFieldValue );
    	service.updateUser( testUser1 );
    	
    	// make sure the change was persisted
    	User userCopy = service.getUserByUsername( testUser1.getUserName() );
    	assertThat( userCopy.getPasswordHash() ).as( "Password field change must be persisted!" ).isEqualTo( newFieldValue );
    	
    }
	
	@Test
	public void getKeyIdsByUsername() {
		
		// create a couple of Keys
    	String key1Id = "OpenVMS";
    	String key1Value = "ForeverForever";
    	String key2Id = "Jaguar";
    	String key2Value = "XJ6";
    	service.createKey( testUser2.getUserName(), key1Id, key1Value );
    	service.createKey( testUser2.getUserName(), key2Id, key2Value );

    	// get the Key IDs
    	List< String > ids = service.getKeyIdsByUsername( testUser2.getUserName() );
		
    	// should have two keys
    	assertThat( ids.size() ).as( "Should be two Key IDs associated with test user #2!" ).isEqualTo( 2 );
		assertThat( ids.contains( key1Id )).as( "Key #1 ID should be one of the key IDs returned!" ).isTrue();
		assertThat( ids.contains( key2Id )).as( "Key #2 ID should be one of the key IDs returned!" ).isTrue();
    	
	}
	
}
