package coordination;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import zookeeper.util.Znode.ServerData;
import zookeeper.util.Znode.SortedServers;

import com.google.protobuf.InvalidProtocolBufferException;

public class GlobalViewServer //implements Runnable
{

	ZookeeperClient zkCli;
	int timeInterval = 2000;
	//volatile boolean terminate = false;

	public GlobalViewServer(ZookeeperClient zkCli, int timeInterval)
	{
		this.zkCli=zkCli;
		this.timeInterval = timeInterval;
	}
	/*
	public void stopNow()
	{
		terminate = true;
	}
	 */
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

	//implement the policy which server can participate i a chain
	void applyEliminationPolicy(List<ServerData> sortedServers)
	{

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
	public void updateSortedServersZnode() throws InvalidProtocolBufferException, KeeperException, InterruptedException
	{
		List<ServerData> sortedServers = sortedServersList();
		applyEliminationPolicy(sortedServers);
		List<Integer> leaderIndexList = leaderIndexList(sortedServers);

		SortedServers.Builder data = SortedServers.newBuilder();
		data.addAllSortedServers(sortedServers);
		data.addAllLeaderIndex(leaderIndexList);

		zkCli.updateSortedServersZnode(data.build());
	}

//	@Override
	public void run1() 
	{
		while(true)
		{
			try 
			{
				updateSortedServersZnode();
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
				throw new RuntimeException("Global view updater Thread Terminated.");
				// TODO Auto-generated catch block
			}
		}	
	}
	// TODO Auto-generated method stub


}
