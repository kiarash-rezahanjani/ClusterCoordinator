package Network;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//import protocol.ConnectionLessChannel;
import protocol.ReceivedMessageCallBack;

public class ConnectionLessChannelTest {
	/*
	static ReceivedMessageCallBack receiver1;
	static ReceivedMessageCallBack receiver2;
	
	static ConnectionLessChannel channel1;
	static ConnectionLessChannel channel2; 
	
	static int port1 = 3333;
	static int port2 = 2223;
	
	static List<String> sendList = new ArrayList<String>();
	static List<String> receiveList = new ArrayList<String>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("1");
		receiver1 = new ReceivedMessageCallBack()
		{

			@Override
			public void received(Object msg, InetSocketAddress socketAddress) {
				// TODO Auto-generated method stub
				System.out.println((String) msg + " from " + socketAddress.toString());
				receiveList.add((String) msg);
			}	

		};
		System.out.println("2");
		receiver2 = new ReceivedMessageCallBack()
		{
			@Override
			public void received(Object msg, InetSocketAddress socketAddress) {
				// TODO Auto-generated method stub
				System.out.println((String) msg + " from " + socketAddress.toString());
				receiveList.add((String) msg);
			}	
		};
		System.out.println("3");
		channel1 = new ConnectionLessChannel(receiver1, port1);
		System.out.println("4");
		channel2 = new ConnectionLessChannel(receiver2, port2);
		System.out.println("5");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		channel1.close();
		channel2.close();
	}

	@Test
	public void testSendReceive() {
		
		try {
		for(int i=0; i<10; i++)
		{
			
			channel1.send(String.valueOf(i) , new InetSocketAddress("127.0.0.1",port2));
			sendList.add(String.valueOf(i));
			Thread.sleep(100);
		}
		
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Send List" + sendList);
		System.out.println("\nReceive List" + receiveList);
		
		assertTrue("Send Receive:", true);
	}
*/

}
