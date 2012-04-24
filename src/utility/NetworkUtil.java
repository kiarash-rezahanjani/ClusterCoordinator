package utility;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtil 
{

	static int port = 3311;
	static InetSocketAddress receiverSocketAddress;
	
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
	
	static public InetSocketAddress parseInetSocketAddress(String socketAddress)
	{
		Pattern p = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
		Matcher m = p.matcher(socketAddress);
		String host="localhost";
		int port=0;
		if (m.matches()) 
		{
		  host = m.group(1);
		  port = Integer.parseInt(m.group(2));
		  //System.out.println(host+ " " +port);
		  return new InetSocketAddress("localhost", port);
		}else
			return null;

	}
}
