package springboot;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias( "sequence" )
@Document( collection = "sequences" )
public class Sequence {

    @Id
    private String id;
 
    private long count;

	public long getCount() {
		return count;
	}

	public String getId() {
		return id;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setId(String id) {
		this.id = id;
	}
    
}
