package rpc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import protocol.MessageType;
import protocol.ProtocolMessage;
import protocol.ReceivedMessageCallBack;
import rpc.udp.SenderReceiver;
//incomplete at the moments just printing
public class SenderReceiverTest {

	static SenderReceiver sr1 ;
	static SenderReceiver sr2 ;

	static List<ProtocolMessage> sendList1 = new ArrayList<ProtocolMessage>();
	static List<ProtocolMessage> receiveList1 = new ArrayList<ProtocolMessage>();

	static List<ProtocolMessage> sendList2 = new ArrayList<ProtocolMessage>();
	static List<ProtocolMessage> receiveList2 = new ArrayList<ProtocolMessage>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ReceivedMessageCallBack callback1 = new ReceivedMessageCallBack()
		{
			@Override
			public void received(Object msg, InetSocketAddress srcSocketAddress) {
				receiveList1.add((ProtocolMessage)msg);
				System.out.println("Receive1");
				printMsg((ProtocolMessage)msg);
				// TODO Auto-generated method stub
				/*				System.out.println("1-- THIS IS THE ONE: RIGHT CALLBACK 1 BRO.");
				ProtocolMessage pmsg = (ProtocolMessage) msg;

				System.out.println("Message Type: " + pmsg.getMessageType());
				System.out.println("ID " + pmsg.getMsgId());
				System.out.println("Content " + pmsg.getMsgContent().toString());
				System.out.println("\n\n");

				 */
			}
		};

		ReceivedMessageCallBack callback2 = new ReceivedMessageCallBack()
		{
			@Override
			public void received(Object msg, InetSocketAddress srcSocketAddress) {
				receiveList2.add((ProtocolMessage)msg);
				System.out.println("Receive2");
				printMsg((ProtocolMessage)msg);
				// TODO Auto-generated method stub
				/*			System.out.println("2-- THIS IS THE ONE: RIGHT CALLBACK 2 BRO.");
				ProtocolMessage pmsg = (ProtocolMessage) msg;


				System.out.println("Message Type: " + pmsg.getMessageType());
				System.out.println("ID " + pmsg.getMsgId());
				System.out.println("Content " + pmsg.getMsgContent().toString());
				System.out.println("\n\n");
				 */

			}
		};

		sr1 = new SenderReceiver(callback1, 1111);
		sr2 = new SenderReceiver(callback2, 2222);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//sr1.close();
		//sr2.close();

	}

	@Test
	public void testSendReceiveObject() {
		try {
			for(int i=0; i<10; i++)
			{
				ProtocolMessage message1 
				= new ProtocolMessage(MessageType.JOIN_ENSEMBLE_REQUEST,  "1->2 : " + i);
				sendList1.add(message1);
				
				System.out.println("Send1");
				printMsg(message1);
				sr1.send("localhost",2222, message1);

				Thread.sleep(200);

				ProtocolMessage message2 
				= new ProtocolMessage(MessageType.JOIN_ENSEMBLE_REQUEST,  "2->1 : " + i);
				sendList2.add(message2);
				
				System.out.println("Send2");
				printMsg(message2);
				sr2.send("localhost",1111, message2);
				
				Thread.sleep(200);

				if(i==49)
				{


					Thread.sleep(1000);

					System.out.println("Send:");
					System.out.println(sendList1.size());
					System.out.println(sendList2.size());
					
					System.out.println("Receive:");
					System.out.println(receiveList1.size());
					System.out.println(receiveList2.size());
					System.out.println(equals(receiveList2, sendList1));
					System.out.println(equals(receiveList1, sendList2));

					try {
						sr1.close();
						sr2.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			assertTrue("Send Receive1:", sendList1.size()!=0);
			assertTrue("Send Receive1:", sendList2.size()!=0);
			assertTrue("Send Receive1:", sendList1.equals(receiveList2));
			assertTrue("Send Receive1:", sendList2.equals(receiveList1));
			System.out.println(sendList1.size()!=0);
			System.out.println(sendList2.size()!=0);
			System.out.println(sendList1.equals(receiveList2));
			System.out.println(sendList2.equals(receiveList1));

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static void printMsg(ProtocolMessage msg)
	{
		System.out.println(msg.getMsgId());
		System.out.println(msg.getMessageType());
		System.out.println(msg.getMsgContent());
	}


	static boolean equals(List<ProtocolMessage> list1 ,List<ProtocolMessage> list2)
	{
		if(list1.size()!=list2.size())
			return false;

		for(ProtocolMessage obj : list1)
		{
			if(!list2.contains(obj))
			{	
				printMsg(obj);
				return false;

			}
		}

		return true;
	}

}
