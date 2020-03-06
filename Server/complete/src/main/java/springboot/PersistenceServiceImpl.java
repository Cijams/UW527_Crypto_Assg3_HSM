package springboot;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceServiceImpl implements PersistenceService {

	@Autowired
	private KeyDao keyDao;

	@Autowired
	private SequenceDao sequenceDao;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public Key createKey(String userName, String keyId, String keyValue) {
		
		Key newKey = new Key();
		newKey.setKeyId( keyId );
		newKey.setValue( keyValue );
		
		User user = getUserByUsername( userName );
		if ( user == null ) return null;	// no user by that ID found!
		
		user.getKeys().add( newKey );
		updateUser( user );

		return newKey;
		
	}


	@Override
	public Key createKey(String userName, String keyId, String keyValue, String publicKeyValue) {
		
		Key newKey = new Key();
		newKey.setKeyId( keyId );
		newKey.setValue( keyValue );
		newKey.setPublicKeyValue(publicKeyValue);

		User user = getUserByUsername( userName );
		if ( user == null ) return null;	// no user by that ID found!
		
		user.getKeys().add( newKey );
		updateUser( user );

		return newKey;
		
	}

	@Override
	public User createUser( String userName, String passwordHash ) {
		
		// create a new User object given the supplied params
		User newUser = new User();
		newUser.setUserName( userName );
		newUser.setPasswordHash( passwordHash );
		
		// persist
		return createUser( newUser );
		
	}

	@Override
	public User createUser( User newUser ) {
		return userDao.save( newUser );
	}

	@Override
	public User deleteKey(String userName, String keyId) {

		User user = getUserByUsername( userName );
		if ( user == null ) return null;	// no user by that ID found!
		
		Key keyToRemove = null;
		
		for ( Key key : user.getKeys() ) {
			
			if ( key.getKeyId().equalsIgnoreCase( keyId ) ) {
				keyToRemove = key;
				break;
			}
			
		}

		if ( keyToRemove != null ) {
			
			user.getKeys().remove( keyToRemove );
			return updateUser( user );
			
		}
		
		return user;
		
	}

	@Override
	public void deleteSequencer(String seqName) {
		
		// get sequence
		Sequence sequence = sequenceDao.findById( seqName ).orElse( null );

		// exists? delete it if it does
		if ( sequence != null ) {
		
			sequenceDao.delete( sequence );
			
		}
		
	}

	@Override
	public void deleteUser( User user ) {
		userDao.delete( user );
		
	}

	@Override
	public List< User > findAllUsers() {
		return userDao.findAll();
	}

	@Override
	public long getCount( String seqName ) {
	    
		// get sequence
		Sequence sequence = sequenceDao.findById( seqName ).orElse( null );
		
		// exists?
		if ( sequence == null ) {
			
			sequence = new Sequence();
			sequence.setId( seqName );
			sequenceDao.save( sequence );
			
		}
		
		// get the current sequencer value
		long sequenceValue = sequence.getCount();
		
		// increment it's counter
		sequence.setCount( sequence.getCount() + 1 );
		sequenceDao.save( sequence );
		
		// return the current value
		return sequenceValue;
	
	}

	@Override
	public String getKeyValueById(String keyId) {
		
		// I'm sure there is a better way to query Mongo for this, but this is quick and simple
		for ( User user : userDao.findAll() ) {
	
			// is the specified key ID associated with this user?
			for ( Key key : user.getKeys() ) {
				if ( key.getKeyId().equalsIgnoreCase( keyId )) return key.getValue();
			}
			
		}
		
		// key wasn't found
		return null;
		
	}

	@Override
	public String getPublicKeyValueById(String keyId) {
		
		// I'm sure there is a better way to query Mongo for this, but this is quick and simple
		for ( User user : userDao.findAll() ) {
	
			// is the specified key ID associated with this user?
			for ( Key key : user.getKeys() ) {
				if ( key.getKeyId().equalsIgnoreCase( keyId )) return key.getPublicValue();
			}
			
		}
		
		// key wasn't found
		return null;
		
	}

	@Override
	public Key getMasterKey() {
		return keyDao.findById( Key.MASTER_KEY_ID ).orElse( null );
	}

	@Override
	public User getUserByUsername( String userName ) {
		return userDao.findById( userName ).orElse( null );
	}

	@Override
	public Key setMasterKey( String value ) {
		
		Key newMasterKey = new Key();
		newMasterKey.setKeyId( Key.MASTER_KEY_ID );
		newMasterKey.setValue( value );
		
		return keyDao.save( newMasterKey );
		
	}

	@Override
	public User updateUser( User user ) {
		return userDao.save( user );
	}


	@Override
	public List<String> getKeyIdsByUsername(String username) {
		
		// get the specified User
		User user = getUserByUsername( username );
		
		// user found?
		if ( user == null ) return new ArrayList< String >();
		
		 return user.getKeys().stream().map( k -> k.getKeyId() ).collect( Collectors.toList() );
		 
	}
	
}