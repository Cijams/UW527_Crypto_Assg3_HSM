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

    @After
    public void cleanUp() throws Exception {
    	
    	// destroy test users
    	service.deleteUser( testUser1 );
    	service.deleteUser( testUser2 );
    	
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
    public void masterKeyPersist() {
    	
    	String masterKeyValue = "AE559426858CA3CF50F32D7B3F881560B6C3B4FA32AC265DDBBD7ABC6FECD5CA";
    	
    	// set the master key first
    	service.setMasterKey( masterKeyValue );
    	
    	// retrieve the master key and verify value set
    	Key masterKey = service.getMasterKey();
    	assertThat( masterKey.getValue() ).as( "Master key value should be set properly!" ).isEqualTo( masterKeyValue );
    	
    }
	
}
