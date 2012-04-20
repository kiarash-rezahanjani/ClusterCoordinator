package coordination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import utility.Znode.EnsembleData;

public class EnsemblesMetaData {

	HashMap<String, String> serverToEnsemble = new HashMap<String, String>();
	HashMap<String, EnsembleData> ensembleToData = new HashMap<String, EnsembleData>();
	List<String> leaderOfEnsembles = new ArrayList<String>();
	String mySocketAddress;

	public EnsemblesMetaData(String socketAddress)
	{
		mySocketAddress = socketAddress;
	}

	/**
	 * Add a new ensemble and update the other data structure. If the ensemble already exists replace it with new one and update the other data structures.
	 * @param ensemblePath
	 * @param data
	 */
	public void put(String ensemblePath, EnsembleData data)
	{

		if(ensembleToData.containsKey(ensemblePath))
			remove(ensemblePath);

		String leader = data.getLeader();
		List<EnsembleData.Member> members = data.getMembersList();	
		ensembleToData.put(ensemblePath, data);

		if(leader==mySocketAddress)
			leaderOfEnsembles.add(ensemblePath);

		for(EnsembleData.Member member : members)
			serverToEnsemble.put(member.getSocketAddress(), ensemblePath);

	}
	/**
	 * Get the ensembles that the server is in charge of the leadership.
	 * @return
	 */
	List<String> getLedEnsembles()
	{
		return leaderOfEnsembles;
	}
	
	public boolean containsKey(String ensemblePath)
	{
		if(ensembleToData.containsKey(ensemblePath))
			return true;
		else
			return false;
	}
	
	public boolean remove(String ensemblePath)
	{
		if(!ensembleToData.containsKey(ensemblePath))
			return false;

		List<EnsembleData.Member> members = ensembleToData.get(ensemblePath).getMembersList();	
		
		for(EnsembleData.Member member : members)
			serverToEnsemble.remove(member.getSocketAddress());

		leaderOfEnsembles.remove(ensemblePath);
		ensembleToData.remove(ensemblePath);	

		return true;
	}
	
	
	//deprecated
	public void refresh()
	{
		for(String key : ensembleToData.keySet())
		{
			serverToEnsemble.clear();
			leaderOfEnsembles.clear();
			
			EnsembleData data = ensembleToData.get(key);
			
			String leader = data.getLeader();
			
			List<EnsembleData.Member> members = data.getMembersList();

			if(leader==mySocketAddress)
				leaderOfEnsembles.add((String)key);

			for(EnsembleData.Member member : members)
				serverToEnsemble.put(member.getSocketAddress(), key);
		}
	}

}
