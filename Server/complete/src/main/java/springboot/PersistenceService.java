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
	 * Create a new user and store to the database
	 * @param userName The username of this new User
	 * @param passwordHash The hash of the new user's password
	 * @return The current state of the new User as persisted to the database
	 */
	public User createUser( String userName, String passwordHash );
	
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
	
	/**
	 * Create a new Key for a specific User
	 * @param userName The ID of the User that will own the new Key
	 * @param keyId The ID of the new Key
	 * @param keyValue The value of the new Key
	 * @return The current state of the newly-created Key
	 */
	public Key createKey( String userName, String keyId, String keyValue );

	/**
	 * Delete a Key
	 * @param userName The ID of the User that "owns" the Key
	 * @param keyId The ID of the Key to delete
	 * @return The current state of the User
	 */
	public User deleteKey( String userName, String keyId );

}
