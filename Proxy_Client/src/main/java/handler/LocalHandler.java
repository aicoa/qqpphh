package handler;

import com.config.ConfigParser;
import com.protocol.ProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;

/**
 * @author aicoa
 * @date 2024/3/1 22:54
 */
public class LocalHandler extends ChannelInboundHandlerAdapter {
    private  ClientHandler clientHandler = null;
    private  String remoteChannelId =null ;
    private ChannelHandlerContext localctx;

    public LocalHandler(ClientHandler clientHandler,String channelId){
        this.clientHandler=clientHandler;
        this.remoteChannelId=channelId;
    }

    public ChannelHandlerContext getLocalctx(){return localctx; }

    //初始化连接

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.localctx=ctx;
        System.out.println(this.getClass()+"\r\n 与本地端口建立成功："+ctx.channel().remoteAddress());
    }

    //读取内网服务器请求和数据--反代场景


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data =(byte[]) msg;
        ProxyMessage proxyMessage = new ProxyMessage();
        proxyMessage.setType(ProxyMessage.type_data);
        HashMap<String,Object> metaData =new HashMap<>();       //ChannelId-ClientKey
        metaData.put("channelId",remoteChannelId);
        metaData.put("clientKey", ConfigParser.get("client-key"));
        proxyMessage.setMetaData(metaData);
        proxyMessage.setData(data);
        //内网服务器响应之后反应给外网服务器端
        this.clientHandler.getCtx().writeAndFlush(proxyMessage);
        System.out.println(this.getClass()+"\r\n 收到本地"+ctx.channel().remoteAddress()+"的数据，数据量为"+data.length+"字节");
    }

    //断开连接

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProxyMessage proxyMessage = new ProxyMessage();
        proxyMessage.setType(ProxyMessage.type_disconnect);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        proxyMessage.setMetaData(metaData);
        this.clientHandler.getCtx().writeAndFlush(proxyMessage);
        System.out.println(this.getClass()+"\r\n 与本地断开连接："+ctx.channel().remoteAddress());
    }
    //连接异常


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println(this.getClass()+"\r\n 连接异常断开");
        cause.printStackTrace();
    }
}
