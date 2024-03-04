package com.aicoa.PortForwardingTool.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author aicoa
 * @date 2024/3/2 16:24
 */
@Component
public class ServerBootStrapHelper {

    /**
     * @Description aicoa
     * @date 2024/3/2 16:24
     * @param [eventLoopGroup,wokerGroup,port,channelInitializer]
     */
    public synchronized void bootStart(EventLoopGroup eventLoopGroup, EventLoopGroup workerGroup,
                                       String serverHost, int serverPort, ChannelInitializer channelInitializer
                                       )throws Exception{
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //连接超时时间，连接公网服务器时可能会超时导致连接失败，最好不要设置
                    //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
            Channel channel = serverBootstrap.bind(serverHost,serverPort).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future ->{
                channel.deregister();
                channel.close();
            });
        }
        catch (Exception e){
            eventLoopGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println(this.getClass()+"\r\n bootStart()中出错");
            e.printStackTrace();
        }
    }

}
