package coordination;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import protocol.Protocol;


import utility.NetworkUtil;
import utility.Znode.EnsembleData;
import utility.Znode.ServerData;
import utility.Znode.ServersGlobalView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class InterProcessCoordinator {


	ZookeeperClient zkCli;
	String mySocketAddress;
	EventWatcher eventWatcher;
	ExecutorService executor;
	//chains
	EnsemblesMetaData ensemblesMetaData;
	//Protocol protocol = new Protocol(this);
	int totalLoad ;//later on replaced by an object containing cpu memory and bandwidth consumption
	ServersGlobalView serversGlobalView;
	Status status = new Status(ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST);
    
	//clients
	//...
	
	//persistence
	//...
	
	public Status getStatusHandle() {
			return status;	
	}

	public class Status
	{
		short status = ServerStatus.ALL_FUNCTIONAL_ACCEPT_REQUEST; 
		 
		public Status(short status)
		{
			this.status = status;
		}
		
		public short getStatus() {
			return status;	
		}	
		public void setStatus(short status) {
			this.status =  status;
		}
	}
		
	public interface ServerStatus
	{
		public static final short FORMING_ENSEMBLE_LEADER_STARTED = 10; //request others to join the ensemble
		short FORMING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT = 11;//wait for all approvals
		short FORMING_ENSEMBLE_LEADER_WAIT_FOR_CONNECTED_SIGNAL = 12;//wait for all to connect to each other
		short FORMING_ENSEMBLE_LEADER_EXEC_ROLL_BACK = 32;//
		
		short FORMING_ENSEMBLE_NOT_LEADER_STARTED = 13; //already sent accept message and wait for connecting
		short FORMING_ENSEMBLE_NOT_LEADER_CONNECTING = 14; //being requested to join an ensemble
		short FORMING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL =15;
		short FORMING_ENSEMBLE_NOT_LEADER_ROLL_BACK_ALL_OPERATIONS = 31;//later: when leader fails in the middle or it cancels the job
		
		short BROKEN_ENSEMBLE = 16; //one of the ensemble that I am member of of is broken
		
		short BROKEN_ENSEMBLE_FINDING_REPLACEMENT = 17; 
		short FIXING_ENSEMBLE_LEADER_WAIT_FOR_ACCEPT = 18; 
		short FIXING_ENSEMBLE_LEADER_WAIT_FOR_CONNECTED_SIGNAL = 19;
		
		short FIXING_ENSEMBLE_NOT_LEADER_CONNECTING = 20;//the broken ensemble is being fixed and I a listening for results from the leader	
		short FIXING_ENSEMBLE_NOT_LEADER_WAIT_FOR_START_SIGNAL = 21;
		
		//later to be completed
		short I_AM_LEAVING_ENSEMBLE = 22;//I leave ensemble and replace myself with another node
		short A_MEMBER_LEAVING_ENSEMBLE = 23;//a member is leaving ensemble wait for new one
		short ALL_FUNCTIONAL_REJECT_REQUEST = 24;	//all fine just server is saturated
		short ALL_FUNCTIONAL_ACCEPT_REQUEST  = 1;//( short) ServerData.Status.ACCEPT_ENSEMBLE_REQUEST.getNumber();// all fine and I can also join a new ensemble
	}

	public InterProcessCoordinator()
	{
		try {
			mySocketAddress = NetworkUtil.getServerSocketAddress();
			ensemblesMetaData = new EnsemblesMetaData(mySocketAddress);
			eventWatcher = new EventWatcher(this);
			zkCli = new ZookeeperClient(eventWatcher);
			zkCli.createServerZnode(getInitialServerData());
			
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	ServerData getInitialServerData()
	{
		ServerData.Builder data = ServerData.newBuilder();
		data.setCapacityLeft(new Random().nextInt(100));
		data.setSocketAddress(NetworkUtil.getServerSocketAddress());
		data.setStat(ServerData.Status.ACCEPT_ENSEMBLE_REQUEST);
		return data.build();
	}

	/**
	 * This method is invoked when the server becomes in charge of updating the global view.
	 */
	public void startGlobalViewUpdater()
	{
		Executors.newSingleThreadExecutor();
		executor.submit(new GlobalViewServer(zkCli, 3000)); 
		//maybe we should create an ephemeral node
	}
	
	public void stopGlobalViewUpdater()
	{
		executor.shutdownNow();
		//delete the ephemeral node
	}
	
	public void setClientLoad(String clientIdentifier, int load)
	{
		//....
		updateTotalLoad(clientIdentifier, load);
	}

	synchronized void  updateTotalLoad(String clientIdentifier, int load)
	{
		// -oldvalue +newvalue
	}

	public List<InetSocketAddress> getSortedCandidates()
	{
		return null;
	}
	
	
//--------------------------
	//@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
