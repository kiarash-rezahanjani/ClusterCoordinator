package zookeeper.util;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import zookeeper.util.Znode.EnsembleData;
import zookeeper.util.Znode.ServerData;
import zookeeper.util.Znode.ServerData.Status;

import com.google.protobuf.InvalidProtocolBufferException;



public class ZkOperation{
/*
	static ZooKeeper zk = null;
	static ZkEventHandler watcher = null;
	String socketAddress = "localhost:2181";
	int sessionTimeOut = 3000;
	String logServiceRootPath = "/logservice";
	String serverRootPath = logServiceRootPath + "/servers";
	String ensembleRootPath = logServiceRootPath + "/ensembles";
	String myNodeName = null;
	String myServerZnodePath = null;
	int zkClientPortNo = 3339 ; 

	//connect to zookeeper and register a wacher object
	public ZkOperation() throws KeeperException, IOException, InterruptedException
	{
		watcher = new ZkEventHandler(); 

		if(zk==null)
		{
			zk = new ZooKeeper(socketAddress, sessionTimeOut, watcher);	

			Stat s = zk.exists(logServiceRootPath, false);
			if(s==null)
				zk.create(logServiceRootPath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

			s = zk.exists(serverRootPath, false);
			if(s==null)
				zk.create(serverRootPath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

			s = zk.exists(ensembleRootPath, false);
			if(s==null)
				zk.create(ensembleRootPath, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

		}

		myServerZnodePath = serverRootPath + "/" + getLocalInetSocket();

	}
	

	String getLocalInetSocket()
	{

		try {
			return InetAddress.getLocalHost().getHostAddress() + ":" + zkClientPortNo;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return "127.0.0.1";
	}


	//deprecated
	public void createServerZnode(ServerData data) throws KeeperException, InterruptedException
	{
		Stat s = zk.exists(myServerZnodePath, false);
		if(s==null)
			zk.create(myServerZnodePath, data.toByteArray(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}


	public void updateServerZnode(ServerData data) throws KeeperException, InterruptedException
	{
*/		/*
		if(myNodeName==null)
			myNodeName = getLocalInetSocket();

		String path = serverRootPath+"/"+myNodeName;

		Stat s = zk.exists(path, false);
		if(s==null)
			zk.create(path, data.toByteArray(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		else
			zk.setData(path, data.toByteArray(), -1);	
		 *//*
		zk.setData(myServerZnodePath, data.toByteArray(), -1);	
	}

	public void deleteServerZnode() throws KeeperException, InterruptedException
	{
		Stat s = zk.exists(myServerZnodePath, false);
		if(s!=null)
			zk.delete(myServerZnodePath, -1);
	}

	//choose subset of all children of server node and sort them, nodes are chosen randomly and there is a lower limit of number of retured candidates 
	//working fine
	public List<ServerData> getEnsembleCandidates() throws KeeperException, InterruptedException, InvalidProtocolBufferException
	{
		float subSetFraction = 0.1f;
		int minCandidates = 5;
		int numCandidates = minCandidates;
		boolean returnAll = false;
		

		List<String> children = zk.getChildren(serverRootPath, false);

		if( minCandidates >= children.size() )
		{
			returnAll=true;
			numCandidates = children.size();
		}else
		{
			if( minCandidates >= subSetFraction*children.size() )
			{
				numCandidates = minCandidates;
			}else
			{
				numCandidates = Math.round((subSetFraction * children.size()) );
			}
		}
		
		List<ServerData> sortedCandidates = new LinkedList<ServerData>(); 
		//System.out.println("Number of children:" + children.size() + "\n");

		int nextCandidateIndex=0;
		int sortedCandidatesSize=0;
		Random rnd = new Random();
		for(int i = 0 ; i < numCandidates; i++)
		{
			
			if(returnAll==true)
				nextCandidateIndex=i;
			else
				nextCandidateIndex=rnd.nextInt(children.size());

			//if the element has been selected start a new iteration
			if( returnAll==false && sortedCandidates.contains(children.get(nextCandidateIndex)) )
			{
				i--;
				continue;
			}


			Stat stat = new Stat();
			byte[] data = zk.getData(serverRootPath + "/" + children.get(i), false, stat);

			ServerData dataObject = ServerData.parseFrom(data);

			sortedCandidatesSize = sortedCandidates.size();

			boolean addedInLoop = false;
			for(int j=0; j<sortedCandidatesSize ;j++)
			{
				if(dataObject.getCapacityLeft() >= sortedCandidates.get(j).getCapacityLeft())
				{	
					sortedCandidates.add(j, dataObject);
					addedInLoop = true;
					break;
				}
			}
			//add first element and the smallest elements to the tail
			if(addedInLoop==false)
				sortedCandidates.add(dataObject);

		}

		return sortedCandidates;
	}

	//ensemble node operation
	public String createEnsembleZnode(EnsembleData data)
	{return "";}

	public void updateEnsembleZnode(String ensemblePath, EnsembleData data)
	{}

	public void deleteEnsembleZnode(String ensemblePath)
	{}

	public void getEnsembleData(String ensemblePath)
	{}

	//forming ensemble
	String getLeader(String ensemblePath)
	{return "";} 

	public boolean znodeExist()
	{return true;}

	public void watchZnode()
	{}
	//for testng
	public void createServerZnode(String name, ServerData data) throws KeeperException, InterruptedException
	{
		System.out.println( "Data: " + data.toString() );

		String path = serverRootPath+"/"+ name;

		Stat s = zk.exists(path, false);
		if(s==null)
			zk.create(path, data.toByteArray(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);


	}

	public void printChildrenStat(String path) throws KeeperException, InterruptedException, InvalidProtocolBufferException
	{
		List<String> children = zk.getChildren(path, false);
		System.out.println("Number of children:" + children.size() + "\n");

		for(int i = 0 ; i < children.size(); i++)
		{

			System.out.println("\n------------CHILD------------\n" + children.get(i));

			Stat stat = new Stat();
			byte[] data = zk.getData(path+"/"+children.get(i), false, stat);

			System.out.println( "No of bytes: " + data.length );

			if(path == serverRootPath)
			{
				ServerData dataObject = ServerData.parseFrom(data);
				System.out.print( "STAT: " + stat.toString() );
				System.out.print( "DATA: " + dataObject.toString() );
			}

			if(path == ensembleRootPath)
			{
				EnsembleData dataObject = EnsembleData.parseFrom(data);
				System.out.print( "STAT: " + stat.toString() );
				System.out.print( "DATA: " + dataObject.toString() );
			}
		}

	}
	
	

	//leader operation

	//failure operation
	public static void main(String[] args)
	{
		try {
			ZkOperation zkOpt = new ZkOperation();
			
			List<ServerData> dataList = new ArrayList<ServerData>();
			
			for(int i=0;i<101;i++)
			{
				int rnd = new Random().nextInt(11);
				
				ServerData.Builder data1 = ServerData.newBuilder();
				data1.setCapacityLeft(rnd);
				data1.setSocketAddress(zkOpt.getLocalInetSocket());
				data1.setStat(Status.ACCEPT_ENSEMBLE_REQUEST);
				
				zkOpt.createServerZnode( String.valueOf(i), data1.build());
				Thread.sleep(200);
			}

			Thread.sleep(2000);

			zkOpt.printChildrenStat(zkOpt.serverRootPath);
			
			List<ServerData> sd = zkOpt.getEnsembleCandidates();

			System.out.println("\n-----------------SORTED-----------------------\n");
		
			for(ServerData i:sd)
			{
				System.out.println(i);
			}
			//System.out.print(zkOpt.getLocalInetSocket());

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





	}
*/
}
