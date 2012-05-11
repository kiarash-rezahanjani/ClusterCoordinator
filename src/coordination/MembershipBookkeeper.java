package coordination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utility.Configuration;
import utility.Znode.EnsembleData;

public class MembershipBookkeeper {

	final Configuration config;
	HashMap<String, EnsembleData> ensemblePathToEnsembleData = new HashMap<String, EnsembleData>();
	final String protocolSocketAddress;
	

	public MembershipBookkeeper(Configuration config)
	{
		this.config = config;
		protocolSocketAddress = config.getProtocolSocketAddress().toString();
	}

	/**
	 * Add a new ensemble and update the other data structure. If the ensemble already exists replace it with new one and update the other data structures.
	 * @param Absolute path to ensemble Znode
	 * @param Previous EnsembleData, If did not exist returns null
	 */
	public EnsembleData put(String ensemblePath, EnsembleData ensembleData)
	{
		return ensemblePathToEnsembleData.put(ensemblePath,  ensembleData) ;
	}
	
	/**
	 * 
	 * @param ensemblePath
	 * @return Previous EnsembleData, If did not exist returns null
	 */
	public EnsembleData remove(String ensemblePath)
	{
		return ensemblePathToEnsembleData.remove(ensemblePath);
	}
	
	/**
	 * Get the ensembles for which this server is the leader.
	 * @return
	 */
	HashMap<String, EnsembleData> getLedEnsembles(){
		HashMap<String, EnsembleData> ledEnsembles = new HashMap<String, EnsembleData>();
		Iterator it = ensemblePathToEnsembleData.entrySet().iterator();
		
		while(it.hasNext()){
			Map.Entry<String, EnsembleData> keyValue = (Map.Entry<String, EnsembleData>) it.next();
			
			if(keyValue.getValue().getLeader()==protocolSocketAddress)
				ledEnsembles.put(keyValue.getKey(), keyValue.getValue());
		}
		return ledEnsembles;
	}
	
	


}
