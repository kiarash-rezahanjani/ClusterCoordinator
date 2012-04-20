package utility;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class NetworkUtil 
{

	static int port = 3311;
	
	public static String getServerSocketAddress() 
	{
		try {
			return InetAddress.getLocalHost().getHostAddress() + ":" + port;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public static InetSocketAddress getInetSocketAddress() 
	{
		try {
			return new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public static String getLocalHostAddress() 
	{
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	//just for testing
	public static String getServerSocketAddress1() 
	{
		return "localhost"+ new Random().nextInt(50000);
	}
}
