package protocol;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Maintain data required by leader for managing in-progress operation 
 * such as forming or repairing ensemble. 
 * @author root
 *
 */
public class LeaderBookKeeper {
	private int ensembleSize = 3;
	
	private HashSet<InetSocketAddress> candidateSet
		= new HashSet<InetSocketAddress>();
	
	private HashMap<InetSocketAddress, Boolean> requestedNodeList 
	= new HashMap<InetSocketAddress, Boolean>();
	
	private HashMap<InetSocketAddress, Boolean> acceptedNodeList 
	= new HashMap<InetSocketAddress, Boolean>();
	
	private HashMap<InetSocketAddress, Boolean> connectedNodeList 
	= new HashMap<InetSocketAddress, Boolean>();
	
	private List<InetSocketAddress> ensembleMembers 
	= new ArrayList<InetSocketAddress>();
	

	/**
	 * Sets a list of all members of the ensemble including the leader.
	 * @param List of all members of the ensemble including the leader
	 */
	public void setEnsembleMembers(List<InetSocketAddress> ensembleMembers) {
		this.ensembleMembers = ensembleMembers;
	}

	/**
	 * Gets list of all members of the ensemble including the leader
	 * @return List of all members of the ensemble including the leader
	 */
	public List<InetSocketAddress> getEnsembleMembers() {
		return ensembleMembers;
	}

	public LeaderBookKeeper(int ensembleSize)
	{
		this.ensembleSize=ensembleSize;
	}
	
	public LeaderBookKeeper()
	{
		this.ensembleSize=3;
	}
	
	public void clear()
	{
		ensembleSize = 3;
		candidateSet.clear();
		requestedNodeList.clear();
		acceptedNodeList.clear();
		connectedNodeList.clear();
	}
	
	public void setEnsembleSize(int size)
	{
		ensembleSize = size;
	}
	
	public void addCandidateList(Collection<InetSocketAddress> candidates)
	{
		candidateSet.addAll(candidates);
	}
	
	public void addCandidate(InetSocketAddress sa)
	{
		candidateSet.add(sa);
	}
	
	public void putRequestedNode(InetSocketAddress sa, Boolean confirmed)
	{
		requestedNodeList.put(sa, confirmed);
	}
	
	public void putAcceptedNode(InetSocketAddress sa, Boolean confirmed)
	{
		acceptedNodeList.put(sa, confirmed);
	}
	
	void putConnectedNode(InetSocketAddress sa, Boolean confirmed)
	{
		connectedNodeList.put(sa, confirmed);
	}
	
	public List<InetSocketAddress> getAcceptedList()
	{//HashMap<InetSocketAddress, Boolean>
		List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
		Iterator it = acceptedNodeList.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<InetSocketAddress, Boolean> entry = (Map.Entry<InetSocketAddress, Boolean>)it.next();
			if(entry.getValue().booleanValue()==true)
			{
				list.add(entry.getKey());
			}
		}
		return list;
	}
	
	public List<InetSocketAddress> getConnectedList()
	{//HashMap<InetSocketAddress, Boolean>
		List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
		Iterator it = connectedNodeList.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<InetSocketAddress, Boolean> entry = (Map.Entry<InetSocketAddress, Boolean>)it.next();
			if(entry.getValue().booleanValue()==true)
			{
				list.add(entry.getKey());
			}
		}
		return list;
	}
	
//-----------
	
	public void removeCandidate(InetSocketAddress sa)
	{
		candidateSet.remove(sa);
	}
	
	public void removeRequestedNode(InetSocketAddress sa)
	{
		requestedNodeList.remove(sa);
	}
	
	public void removeAcceptedNode(InetSocketAddress sa)
	{
		acceptedNodeList.remove(sa);
	}
	
	public void removeConnectedNode(InetSocketAddress sa)
	{
		connectedNodeList.remove(sa);
	}
	
	public boolean isEmpty()
	{
		if(candidateSet.isEmpty() && requestedNodeList.isEmpty() 
				&& acceptedNodeList.isEmpty() && connectedNodeList.isEmpty())
			return true;
		else
			return false;
	}
//----------------
	public boolean isAcceptedComplete()
	{
		return isComplete(acceptedNodeList.values());
	}
	
	public boolean isConnectedComplete()
	{
		return isComplete(connectedNodeList.values());
	}
	
	boolean isComplete(Collection<Boolean> values)
	{
		boolean complete = false;
		int count = 0;
		
		for(Boolean value : values)
		{
			if(value.booleanValue()==true)
				count++;
			
			if(count >= ensembleSize-1)
			{
				complete = true;
				break;
			}
		}
		return complete;
	}
	//-------------------------------
	
	
	
}
