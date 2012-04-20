package protocol;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class LeaderBookKeeper {
	private int ensembleSize = 3;
	
	private HashSet<InetSocketAddress> candidateSet
		= new HashSet<InetSocketAddress>();
	
	private HashMap<InetSocketAddress, Boolean> requestedNodeList 
	= new HashMap<InetSocketAddress, Boolean>();
	
	private HashMap<InetSocketAddress, Boolean> accpetedNodeList 
	= new HashMap<InetSocketAddress, Boolean>();
	
	private HashMap<InetSocketAddress, Boolean> connectedNodeList 
	= new HashMap<InetSocketAddress, Boolean>();


	
	public LeaderBookKeeper(int ensembleSize)
	{
		this.ensembleSize=ensembleSize;
	}
	
	public void clear()
	{
		ensembleSize = 3;
		candidateSet.clear();
		requestedNodeList.clear();
		accpetedNodeList.clear();
		connectedNodeList.clear();
	}
	
	public void setEnsembleSize(int size)
	{
		ensembleSize = size;
	}
	
	public void addCandidate(InetSocketAddress sa)
	{
		candidateSet.add(sa);
	}
	
	public void putRequestedNode(InetSocketAddress sa, Boolean confirmed)
	{
		requestedNodeList.put(sa, confirmed);
	}
	
	public void putAccpetedNode(InetSocketAddress sa, Boolean confirmed)
	{
		requestedNodeList.put(sa, confirmed);
		
		
	}
	
	public boolean isAcceptedComplete(Collection<Boolean> values)
	{
		return isComplete(values);
	}
	
	public boolean isConnectedComplete(Collection<Boolean> values)
	{
		return isComplete(values);
	}
	
	boolean isComplete(Collection<Boolean> values)
	{
		boolean complete = false;
		int count = 0;
		
		for(Boolean value : values)
		{
			if(value.booleanValue()==true)
				count++;
			
			if(count >= ensembleSize)
			{
				complete = true;
				break;
			}
		}
		return complete;
	}
	
	void putConnectedNode(InetSocketAddress sa, Boolean confirmed)
	{
		requestedNodeList.put(sa, confirmed);
	}
	
//-----------
	
	public void removeCandidate(InetSocketAddress sa)
	{
		candidateSet.add(sa);
	}
	
	public void removeRequestedNode(InetSocketAddress sa)
	{
		requestedNodeList.remove(sa);
	}
	
	public void removeAccpetedNode(InetSocketAddress sa)
	{
		requestedNodeList.remove(sa);
	}
	
	public void removeConnectedNode(InetSocketAddress sa)
	{
		requestedNodeList.remove(sa);
	}
//----------------
	
	
}
