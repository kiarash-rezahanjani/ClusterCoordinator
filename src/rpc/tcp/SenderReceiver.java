package rpc.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;

import protocol.ReceivedMessageCallBack;


public class SenderReceiver 
{
	private ReceivedMessageCallBack callback;

	private tcpClient sender ;
	private tcpServer receiver ;

	public SenderReceiver(ReceivedMessageCallBack callback)
	{
		this.callback = callback;
		sender = new tcpClient();
		receiver = new tcpServer(callback);
		try {
			receiver.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public SenderReceiver(ReceivedMessageCallBack callback, int port)
	{
		this.callback = callback;
		sender = new tcpClient();
		receiver = new tcpServer(port, callback);
		try {
			receiver.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void send(InetSocketAddress destination, Object message)
	{
		//for(int i=0; i<2; i++)
			try {
				sender.sendAsyncMessage(destination, message);
		//		break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void send(String hostAddress, int port, Object message)
	{
		send(new InetSocketAddress(hostAddress, port ), message);
	}
	
	public void close()
	{
		sender.stop();
		receiver.stop();
	}


}
