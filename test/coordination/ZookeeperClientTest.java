package coordination;
import static org.junit.Assert.*;

import java.util.Random;

import org.apache.zookeeper.KeeperException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import coordination.GlobalViewServer;
import coordination.ZookeeperClient;

import zookeeper.util.Znode.ServerData;
import zookeeper.util.Znode.EnsembleData;
import zookeeper.util.Znode.ServerData.Status;


public class ZookeeperClientTest {

	static ZookeeperClient zkCli;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.print("ZookeeperClientTest JUNIT TEST STARTED...");
		zkCli = new ZookeeperClient(null);
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		zkCli.close();
	}

	@Test
	public final void testCreateServerZnodeServerData() {
		ServerData.Builder data = ServerData.newBuilder();
		data.setCapacityLeft(new Random().nextInt(100));
		data.setSocketAddress(Integer.toString(new Random().nextInt(100000)));
		data.setStat(Status.ACCEPT_ENSEMBLE_REQUEST);
		try {
			ServerData dataWrite = data.build();
			zkCli.createServerZnode(dataWrite);
			ServerData dataRead = zkCli.getServerZnodeData(zkCli.getMyServerZnodePath());
			//assertTrue("Create Server Node test.",0==1);
			print("Server Write:", dataWrite);
			print("Server Read", dataRead );
			//System.out.print(dataWrite.equals(dataRead));
			assertTrue("Create Server Node test.",dataWrite.equals(dataRead));
			
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void print(String message, ServerData sd)
	{
		System.out.print(message + ": " );
		System.out.print(sd.getCapacityLeft() + " " );
		System.out.print(sd.getSocketAddress() + " " );
		System.out.print(sd.getStat() + " \n" );
	}
	@Test
	public final void testUpdateServerZnode()  {
		ServerData.Builder data = ServerData.newBuilder();
		data.setCapacityLeft(new Random().nextInt(100));
		data.setSocketAddress(Integer.toString(new Random().nextInt(100000)));
		data.setStat(Status.ACCEPT_ENSEMBLE_REQUEST);
		try {
			
			ServerData dataWrite = data.build();
			zkCli.updateServerZnode(dataWrite);
			ServerData dataRead = zkCli.getServerZnodeData(zkCli.getMyServerZnodePath());
			assertTrue("Update Server Node test.",dataWrite.equals(dataRead));
			
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public final void testDeleteServerZnode() {
		ServerData.Builder data = ServerData.newBuilder();
		data.setCapacityLeft(new Random().nextInt(100));
		data.setSocketAddress(Integer.toString(new Random().nextInt(100000)));
		data.setStat(Status.ACCEPT_ENSEMBLE_REQUEST);
		try {
			zkCli.createServerZnode(data.build());
			zkCli.deleteServerZnode();
			assertTrue("Delete Server Node Test", !zkCli.exists(zkCli.getMyServerZnodePath()));
			
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Ignore
	public final void testCreateEnsembleZnode() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testCreateMyEnsembleEmphemeralZnode() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testUpdateEnsembleZnode() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testDeleteEnsembleZnode() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testGetEnsembleData() {
		fail("Not yet implemented"); // TODO
	}

}
