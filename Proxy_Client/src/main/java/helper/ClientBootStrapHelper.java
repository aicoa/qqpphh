package helper;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

/**
 * @author aicoa
 * @date 2024/3/2 0:41
 */
public class ClientBootStrapHelper {
    private Channel channel  = null ;
    public synchronized void start(EventLoopGroup workerGroup, ChannelInitializer channelInitializer,String host,int port){
        if (host==null || port==0){
            System.out.println("配置信息有误");
            return;
        }
        try {
            io.netty.bootstrap.Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(channelInitializer);
            channel = bootstrap.connect(host,port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future ->{
               channel.deregister();
               channel.close();
            });
        }
        catch (Exception e){
            close();
            workerGroup.shutdownGracefully();
            System.out.println(this.getClass()+"\r\n 关闭线程组内所有连接");
            e.printStackTrace();
        }

    }

    public synchronized void close(){
        if (channel != null) channel.close();
    }

}
