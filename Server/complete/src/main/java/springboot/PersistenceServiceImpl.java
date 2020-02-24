package springboot;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceServiceImpl implements PersistenceService {

	@Autowired
	private KeyDao keyDao;

	@Autowired
	private UserDao userDao;

	@Override
	public User createUser( User newUser ) {
		return userDao.save( newUser );
	}

	@Override
	public User getUserByUsername( String userName ) {
		return userDao.findById( userName ).orElse( null );
	}

	@Override
	public User updateUser( User user ) {
		return userDao.save( user );
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
	public Key getMasterKey() {
		return keyDao.findById( Key.MASTER_KEY_ID ).orElse( null );
	}

	@Override
	public Key setMasterKey( String value ) {
		
		Key newMasterKey = new Key();
		newMasterKey.setKeyId( Key.MASTER_KEY_ID );
		newMasterKey.setValue( value );
		
		return keyDao.save( newMasterKey );
		
	}

}