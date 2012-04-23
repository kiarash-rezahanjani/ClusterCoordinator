package protocol;

import java.io.Serializable;
import java.net.InetSocketAddress;

public abstract class AbstractMessage  implements Serializable{
	
	long msgId;
	InetSocketAddress srcSocketAddress;
	
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

	public InetSocketAddress getSrcSocketAddress() {
		return srcSocketAddress;
	}

	public void setSrcSocketAddress(InetSocketAddress srcSocketAddress) {
		this.srcSocketAddress = srcSocketAddress;
	}
}
