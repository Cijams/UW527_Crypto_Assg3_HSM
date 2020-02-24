package springboot;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias( "user" )
@Document( collection = "users" )
public class User {

	@Id
	private String userName;
	
	private String passwordHash;
	
	private Set<Key> keys = new HashSet<>();

	@Override
	public boolean equals( final Object obj ) {

		// obvious equality checks
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( ! ( obj instanceof User ) ) return false;

		// verify userName (primary "key") matches
		final User other = ( User ) obj;
		if ( userName == null ) {
			if ( other.userName != null ) {
				return false;
			}
		} 
		else if ( !userName.equals( other.userName ) ) {
			return false;
		}

		return true;

	}

	public Set<Key> getKeys() {
		return keys;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getUserName() {
		return userName;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
