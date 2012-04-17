package coordination;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
}
