package coordination;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import utility.Znode.ServerData;
import utility.Znode.ServersGlobalView;

import com.google.protobuf.InvalidProtocolBufferException;

public class GlobalViewServer implements Runnable
{

	ZookeeperClient zkCli;
	int timeInterval = 2000;
	//volatile boolean terminate = false;

	public GlobalViewServer(ZookeeperClient zkCli, int updateTimeInterval)
	{
		this.zkCli=zkCli;
		this.timeInterval = timeInterval;
	}

	//sort all the servers based on the capacity left using insertion sort, those with max capacity come in the beginning
	List<ServerData> sortedServersList() throws KeeperException, InterruptedException, InvalidProtocolBufferException
	{
		List<ServerData> sortedServers = new LinkedList<ServerData>(); 
		List<String> children = zkCli.getServerList();

		for(int i = 0 ; i < children.size(); i++)
		{
			ServerData serverData = zkCli.getServerZnodeDataByNodeName(children.get(i));

			boolean addedInLoop = false;
			for(int j=0; j < sortedServers.size() ;j++)
			{
				if(serverData.getCapacityLeft() >= sortedServers.get(j).getCapacityLeft())
				{	
					sortedServers.add(j, serverData);
					addedInLoop = true;
					break;
				}
			}

			//add first element and the smallest elements to the tail
			if(addedInLoop==false)
				sortedServers.add(serverData);
		}

		return sortedServers;
	}

	//implement the policy of which servers can participate in a chain. Remove those that do not accept ensemble request.
	void applyEliminationPolicy(List<ServerData> sortedServers)
	{
		for(int i=0; i<sortedServers.size(); i++)
		{
			if(sortedServers.get(i).getStat() != ServerData.Status.ACCEPT_ENSEMBLE_REQUEST)
			{	
				sortedServers.remove(i);
				i--;
			}
			
		}

	}

	//determine the leaders
	List<Integer> leaderIndexList(List<ServerData> sortedServers)
	{
		List<Integer> leaderIndexList = new ArrayList<Integer>();

		for(int i = 0; i < sortedServers.size(); i++)
			if(i%3==0 /*&& sortedServers.size()-i>=3*/)
				leaderIndexList.add(i);

		return leaderIndexList;
	}

	//update the sortedServers node with sortedServers and the index of the leaders, each leader is in charge of all nodes till next leader in the list
	public void updateServersGlobalViewZnode() throws InvalidProtocolBufferException, KeeperException, InterruptedException
	{
		List<ServerData> sortedServers = sortedServersList();
		applyEliminationPolicy(sortedServers);
		List<Integer> leaderIndexList = leaderIndexList(sortedServers);

		ServersGlobalView.Builder data = ServersGlobalView.newBuilder();
		data.addAllSortedServers(sortedServers);
		data.addAllLeaderIndex(leaderIndexList);

		zkCli.updateServersGlobalViewZnode(data.build());
	}

//	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{
				updateServersGlobalViewZnode();
				Thread.sleep(timeInterval);
			} catch (InvalidProtocolBufferException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeeperException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) 
			{
				throw new RuntimeException("Global View Task terminated!");
				// TODO Auto-generated catch block
			}
		}	
	}
	// TODO Auto-generated method stub


}
