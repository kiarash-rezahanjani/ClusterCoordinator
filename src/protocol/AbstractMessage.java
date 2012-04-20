package protocol;

import java.io.Serializable;

public abstract class AbstractMessage  implements Serializable{
	
	long msgId;
	
	public AbstractMessage(long msgId)
	{
		this.msgId = msgId;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}


	
	

}
