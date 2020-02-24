package springboot;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias( "key" )
@Document( collection = "keys" )
public class Key {

	public static final String MASTER_KEY_ID = "master_key";
	
	@Id
    private String keyId;
	
	private String value;
	
	@Override
	public boolean equals( final Object obj ) {

		// obvious equality checks
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( ! ( obj instanceof Key ) ) return false;

		// verify key ID (primary "key") matches
		final Key other = ( Key ) obj;
		if ( keyId == null ) {
			if ( other.keyId != null ) {
				return false;
			}
		} 
		else if ( !keyId.equals( other.keyId ) ) {
			return false;
		}

		return true;

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
		
}
