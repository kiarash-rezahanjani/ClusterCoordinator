package utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Configuration {

	int protocolPort;
	int bufferServerPort;
	InetSocketAddress protocolSocketAddress;
	InetSocketAddress bufferServerSocketAddress;

	String configDirectory = "configuration";

	String defaultPropertiesFile = "defaultProperties";
	static String applicationPropertiesFile = "applicationProperties";

	static String defaultPropertiesPath;
	static String applicationPropertiesPath;

	static String zkNameSpace;
	static String zkServersRoot;
	static String zkClientRoot;
	static String zkEnsemblesRoot;
	static String zkServersGlobalViewRoot;
	static String zkConnectionString;
	static int zkSessionTimeOut;

	/**
	 * Load properties from the default properties file (configuration/applicationProperties).
	 */
	public Configuration()
	{
		this(applicationPropertiesFile);
	}

	/**
	 * Load properties from the given properties file. File should be place in configuration folder
	 */
	public Configuration(String applicationPropertiesFile)
	{	
		defaultPropertiesPath = configDirectory + System.getProperty("file.separator") + defaultPropertiesFile;
		applicationPropertiesPath = configDirectory + System.getProperty("file.separator") + applicationPropertiesFile;
		// create and load default properties
		Properties defaultProperty =  new Properties();
		Properties applicationProperties;
		FileInputStream input;
		try {

			input = new FileInputStream(defaultPropertiesPath);
			defaultProperty.load(input);
			input.close();

			zkNameSpace = defaultProperty.getProperty("zkNameSpace");
			zkServersRoot = defaultProperty.getProperty("zkServersRoot");
			zkClientRoot = defaultProperty.getProperty("zkClientRoot");
			zkEnsemblesRoot = defaultProperty.getProperty("zkEnsemblesRoot");
			zkServersGlobalViewRoot = defaultProperty.getProperty("zkServersGlobalViewRoot");
			zkConnectionString = defaultProperty.getProperty("zkConnectionString");
			zkSessionTimeOut = Integer.parseInt( defaultProperty.getProperty("zkSessionTimeOut") );
			
			// create application properties with default
			applicationProperties = new Properties(defaultProperty);
			input = new FileInputStream(applicationPropertiesPath);
			applicationProperties.load(input);
			input.close();

			protocolPort = Integer.parseInt( applicationProperties.getProperty("protocol_port") );
			bufferServerPort = Integer.parseInt( applicationProperties.getProperty("buffer_server_port") );




			System.out.println(protocolPort);
			System.out.println(bufferServerPort);

			protocolSocketAddress = new InetSocketAddress("localhost",protocolPort);
			bufferServerSocketAddress = new InetSocketAddress("localhost",bufferServerPort);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public int getZkSessionTimeOut() {
		return zkSessionTimeOut;
	}

	public  String getZkConnectionString() {
		return zkConnectionString;
	}

	public  String getZkServersGlobalViewRoot() {
		return zkServersGlobalViewRoot;
	}

	public  String getZkNameSpace() {
		return zkNameSpace;
	}

	public  String getZkServersRoot() {
		return zkServersRoot;
	}

	public  String getZkClientRoot() {
		return zkClientRoot;
	}

	public  String getZkEnsemblesRoot() {
		return zkEnsemblesRoot;
	}

	/**
	 * IP address is the wild card address and port is specified in the configuration file.
	 * @return
	 */
	public InetSocketAddress getProtocolSocketAddress()
	{
		return protocolSocketAddress;
	}

	/**
	 * IP address is the wild card address and port is specified in the configuration file.
	 * @return
	 */
	public InetSocketAddress getBufferServerSocketAddress()
	{
		return bufferServerSocketAddress;
	}

	public int getBufferServerPort()
	{
		return bufferServerPort; 
	}

	public int getProtocolPort()
	{
		return protocolPort; 
	}
/*	
	public static void main(String[] args)
	{
		System.out.println( System.getProperty("user.dir") );
		Configuration conf = new Configuration();

		System.out.println("server address:" + conf.getBufferServerSocketAddress().toString());
		System.out.println("protocol address:" + conf.getProtocolSocketAddress().toString());
		System.out.println("protocol address:" + conf.getZkConnectionString() + " " + conf.getZkClientRoot() + " " + conf.getZkEnsemblesRoot() + " " + conf.getZkNameSpace()
				+ " " + conf.getZkServersGlobalViewRoot() + " " + conf.getZkServersRoot() + " " + conf.getZkSessionTimeOut());

	}
	*/ 
}
