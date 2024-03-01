package handler;

import com.protocol.ProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;

/**
 * @author aicoa
 * @date 2024/3/1 23:38
 * @description 客户端心跳处理
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private EventLoopGroup workerGroup = null;
    public HeartBeatHandler() {
    }
    public HeartBeatHandler(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    //心跳包
    private  static final ProxyMessage heart_beat = new ProxyMessage(ProxyMessage.type_keeplive);

    //客户端写超时时间触发
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println(this.getClass()+"\r\n 写超时，发送心跳包");
        ctx.writeAndFlush(heart_beat);
    }
    //异常处理，发生异常时直接关闭服务器连接(比如发送心跳失败，可能服务器下线了)
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(this.getClass()+"\r\n 连接异常，已中断");
        workerGroup.shutdownGracefully();
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }
}
