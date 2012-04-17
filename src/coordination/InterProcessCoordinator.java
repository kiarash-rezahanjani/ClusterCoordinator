package coordination;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import zookeeper.util.Znode.EnsembleData;
import zookeeper.util.Znode.ServerData;
import java.util.concurrent.atomic.AtomicInteger;

public class InterProcessCoordinator implements Runnable, Watcher {

	ZookeeperClient zkCli;
	String mySocketAddress;
	EventWatcher eventWatcher;
	GlobalViewUpdator gvu;
	ExecutorService executor;
	//chains
	SimpleEnsembleMap ensembleMap;
	int totalLoadOfService ;
    
	//clients

	//persistence
	//...

	public InterProcessCoordinator()
	{
		try {
			mySocketAddress = NetworkUtil.getServerSocketAddress();
			ensembleMap = new SimpleEnsembleMap(mySocketAddress);
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
		data.setCapacityLeft(new Random().nextInt(50));
		data.setSocketAddress(NetworkUtil.getServerSocketAddress());
		data.setStat(ServerData.Status.ACCEPT_ENSEMBLE_REQUEST);
		return data.build();
	}

	void startGlobalViewUpdater()
	{
	//	gvu = new Thread(new GlobalViewServer(zkCli, 3000));
	//	gvu.start();
	}
	
	void stopGlobalViewUpdater()
	{
	//	gvu.interrupt();
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

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub


	}
//Forming chain----------
	boolean sendJoinRequestTo(String socketAddress)
	{
		return true;
	}
	
	void acceptJoinRequestFrom(String socketAddress)
	{
		
	}
	
	void rejectJoinRequestFrom(String socketAddress)
	{
		
	}
//Failure-------------------
//--------------------------
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
