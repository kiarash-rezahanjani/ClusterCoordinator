package rpc.tcp;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import protocol.ProtocolMessage;
import protocol.ReceivedMessageCallBack;

public class tcpServerHandler extends SimpleChannelHandler {

	ReceivedMessageCallBack callback;
	
	public tcpServerHandler(ReceivedMessageCallBack callback)
	{
		this.callback = callback;
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            e.getCause().printStackTrace();
            e.getChannel().close();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
          //  System.out.println("SERVER: Message from:" + e.getRemoteAddress() + " Content:" + e.getMessage().toString());
    	
    	callback.received(e.getMessage(), (InetSocketAddress) e.getRemoteAddress());
    	Thread.sleep(1);
    }

}
