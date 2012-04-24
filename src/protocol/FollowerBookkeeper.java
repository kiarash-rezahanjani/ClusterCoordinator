package protocol;

import java.net.InetSocketAddress;

public class FollowerBookkeeper {

	InetSocketAddress leader;
	
	public FollowerBookkeeper(InetSocketAddress leader) {
		this.leader = leader;
	}
	
	public FollowerBookkeeper() {
	
	}
	
	public InetSocketAddress getLeader() {
		return leader;
	}

	public void setLeader(InetSocketAddress leader) {
		this.leader = leader;
	}

	public void clear()
	{
		leader=null;
	}
	
	public boolean isEmpty()
	{
		return leader==null;
	}
	
}
