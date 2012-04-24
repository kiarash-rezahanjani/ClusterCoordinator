package coordination;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.KeeperException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import utility.Configuration;
import utility.Znode.ServerData;
import utility.Znode.ServersGlobalView;

public class GlobalViewServerTest {

	static ZookeeperClient zkCli;
	static GlobalViewServer gvs;
	static Configuration config = new Configuration();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.print("GlobalViewServerTest JUNIT TEST STARTED...");
		zkCli = new ZookeeperClient(null,config);
		gvs = new GlobalViewServer(zkCli, 2000);

	}
	
	@Test
	public final void testUpdateSortedServersZnode()
	{
		List<ServerData> serverList = new ArrayList<ServerData>();
		List<ServerData> sortedServerList = new ArrayList<ServerData>();
		boolean created=false;
		boolean testResult = true;
		
		for(int i=0;i<101;i++)
		{
			int rndCapacity = new Random().nextInt(90) + 10;
			int rndnameNode = new Random().nextInt(50000);
			
			ServerData.Builder data1 = ServerData.newBuilder();
			data1.setCapacityLeft(rndCapacity);
			data1.setSocketAddress("localhost"+ rndnameNode);
			data1.setStat(ServerData.Status.ACCEPT_ENSEMBLE_REQUEST);
			ServerData sd = data1.build();
			try {
				created = zkCli.createServerZnode(  String.valueOf(rndnameNode), sd);
				if(created==true)
					serverList.add(sd);
				Thread.sleep(50);	
				//System.out.println(created);
			} catch (KeeperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		try {
			gvs.updateServersGlobalViewZnode();
			ServersGlobalView sortedServer = zkCli.getServersGlobalView();
			sortedServerList = sortedServer.getSortedServersList();
			
			//if the size not equal
			if(serverList.size()!=sortedServerList.size())
				testResult=false;
		
			for(int i = 0; i<sortedServerList.size();i++)
			{
				//elements of the list are not properly sorted
				if(i!=sortedServerList.size()-1 && sortedServerList.get(i).getCapacityLeft()<sortedServerList.get(i+1).getCapacityLeft())
					testResult=false;
				
				//initial and fetched list are not the same
				if(!serverList.contains(sortedServerList.get(i)))
					testResult=false;
				
			}
			
			assertTrue("Global View Test: ", testResult==true);
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		zkCli.close();
		gvs=null;

	}



}
