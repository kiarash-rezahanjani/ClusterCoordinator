package rpc.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

import protocol.MessageType;
import protocol.ProtocolMessage;
import protocol.ReceivedMessageCallBack;


public class Test {

	/**
	 * @param args
	 */

	static void checkClientServerCommunication()
	{
		ReceivedMessageCallBack callback = new  ReceivedMessageCallBack()
		{
			@Override
			public void received(Object msg, InetSocketAddress socketAddress) {
				// TODO Auto-generated method stub
				ProtocolMessage pmsg = (ProtocolMessage) msg;
				System.out.println("Message Type: " + pmsg.getMessageType());
				System.out.println("ID " + pmsg.getMsgId());
				System.out.println("ID " + pmsg.getMsgContent().toString());
			}
		};

		tcpServer server = new tcpServer(5555, callback);
		tcpClient client = new tcpClient();

		for(int i=0; i<5;i++)
			try {
				server.start();
				client.sendAsyncMessage("localhost", 5555, new ProtocolMessage(MessageType.JOIN_ENSEMBLE_REQUEST,  "Adios M F"+(i+20)));
				Thread.sleep(1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		server.stop();
		client.stop();

	}

	static void checkCheckSenerReceiver()
	{
		ReceivedMessageCallBack callback1 = new ReceivedMessageCallBack()
		{
			@Override
			public void received(Object msg, InetSocketAddress srcSocketAddress) {
				// TODO Auto-generated method stub
				System.out.println("1-- THIS IS THE ONE: RIGHT CALLBACK 1 BRO.");
				ProtocolMessage pmsg = (ProtocolMessage) msg;

				System.out.println("Message Type: " + pmsg.getMessageType());
				System.out.println("ID " + pmsg.getMsgId());
				System.out.println("Content " + pmsg.getMsgContent().toString());
				System.out.println("\n\n");


			}
		};

		ReceivedMessageCallBack callback2 = new ReceivedMessageCallBack()
		{
			@Override
			public void received(Object msg, InetSocketAddress srcSocketAddress) {
				// TODO Auto-generated method stub
				System.out.println("2-- THIS IS THE ONE: RIGHT CALLBACK 2 BRO.");
				ProtocolMessage pmsg = (ProtocolMessage) msg;

	
				System.out.println("Message Type: " + pmsg.getMessageType());
				System.out.println("ID " + pmsg.getMsgId());
				System.out.println("Content " + pmsg.getMsgContent().toString());
				System.out.println("\n\n");


			}
		};

		SenderReceiver sr1 = new SenderReceiver(callback1, 1111);
		SenderReceiver sr2 = new SenderReceiver(callback2, 2222);


		for(int i=0; i<1; i++)
		{
			ProtocolMessage message1 
			 	= new ProtocolMessage(MessageType.JOIN_ENSEMBLE_REQUEST,  "1->2 : " + i);
			sr1.send("localhost",2222, message1);
			
			ProtocolMessage message2 
		 	= new ProtocolMessage(MessageType.JOIN_ENSEMBLE_REQUEST,  "2->1 : " + i);
			sr2.send("localhost",1111, message2);
			
		}

		sr1.close();
		sr2.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//checkClientServerCommunication();
		checkCheckSenerReceiver();

	}

}
