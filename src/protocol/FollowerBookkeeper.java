package protocol;

import java.net.InetSocketAddress;
import java.util.List;

public class FollowerBookkeeper {

	InetSocketAddress leader;
	List<InetSocketAddress> ensembleMembers;
	String ensemblePath;

	public List<InetSocketAddress> getEnsembleMembers() {
		return ensembleMembers;
	}

	public void setEnsembleMembers(List<InetSocketAddress> ensembleMembers) {
		this.ensembleMembers = ensembleMembers;
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
	
	public String getEnsemblePath() {
		return ensemblePath;
	}
	
	public void setEnsemblePath(String ensemblePath) {
		this.ensemblePath = ensemblePath;
	}
}
