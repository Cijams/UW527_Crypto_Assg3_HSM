package springboot;

import java.util.List;

public interface PersistenceService {

	/**
	 * Create a new user and store to the database
	 * @param newUser The new user to create
	 * @return The current state of the User
	 */
	public User createUser( User newUser );
	
	/**
	 * Get all Users stored in the database
	 * @return All Users
	 */
	public List< User > findAllUsers();
	
	/**
	 * Get a specific User by username
	 * @param userName The username of the User to obtain
	 * @return The User with the matching username
	 */
	public User getUserByUsername( String userName );
	
	/**
	 * Convenience method for saving User changes (uses same method as "createUser")
	 * @param user The User to update
	 * @return The current state of the User
	 */
	public User updateUser( User user );
	
	/**
	 * Remove a User from the database
	 * @param user The User to delete from the database
	 */
	public void deleteUser( User user );
	
	/**
	 * Get the "master" Key from the database
	 * @return The master Key
	 */
	public Key getMasterKey();
	
	/**
	 * Set the "master" Key in the database (there can only be one master key at a time)
	 * @param value The value of the Key
	 * @return The new master Key
	 */
	public Key setMasterKey( String value );
	
}
