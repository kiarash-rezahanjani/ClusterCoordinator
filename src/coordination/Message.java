package coordination;

import java.io.Serializable;

public abstract class Message implements Serializable 
{
	long id;
	
	public Message(long id)
	{
		this.id = id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public long getId()
	{
		return id;
	}
}
