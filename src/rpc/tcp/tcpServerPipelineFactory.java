package rpc.tcp;


import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import protocol.ReceivedMessageCallBack;

public class tcpServerPipelineFactory  implements ChannelPipelineFactory{

	ReceivedMessageCallBack callback;
	
	tcpServerPipelineFactory(ReceivedMessageCallBack callback)
	{
		this.callback = callback;
	}
	
	@Override
    public ChannelPipeline getPipeline() throws Exception {
            ChannelPipeline p = Channels.pipeline();
            p.addLast("decoder", new ObjectDecoder());
            p.addLast("encoder", new ObjectEncoder());
            p.addLast("handler", new tcpServerHandler(callback));
            return p;
    }


}
