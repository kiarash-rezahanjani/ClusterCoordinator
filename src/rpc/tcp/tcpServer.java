package rpc.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import protocol.ReceivedMessageCallBack;

//import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class tcpServer {
        
        public static final int DEFAULT_PORT = 9999;
        
        private int port;
        
        private ReceivedMessageCallBack callback;
        
        public tcpServer(ReceivedMessageCallBack callback) {
                this(DEFAULT_PORT, callback);
        }
        
        public tcpServer(int port, ReceivedMessageCallBack callback) {
                this.port = port;
                this.callback = callback;
        }
        
        void start() throws IOException {

        }
        
        public void stop() {
     
        }
        
        public boolean isRunning() {
                return false;
        }
}
