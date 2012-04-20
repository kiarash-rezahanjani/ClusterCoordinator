package rpc.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

//import org.apache.commons.lang3.time.StopWatch;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import protocol.ProtocolMessage;

public final class tcpClient {

	public tcpClient() {
	

	}

	public void sendAsyncMessage(String hostAddress, int port, final Object message) throws IOException {
		
		sendAsyncMessage(new InetSocketAddress( hostAddress,  port),  message);
	}
	
	public void sendAsyncMessage(final InetSocketAddress iNetSocketAddress, final Object message) throws IOException {
     
	}
    
	public void stop() {
	}

}
