package com.aicoa.PortForwardingTool.server.handler;

import com.aicoa.PortForwardingTool.common.config.ConfigParser;
import com.aicoa.PortForwardingTool.common.protocol.ProxyMessage;
import com.aicoa.PortForwardingTool.server.ServerBootStrapHelper;
import com.aicoa.PortForwardingTool.web.service.ClientService;
import com.aicoa.PortForwardingTool.web.service.LogService;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aicoa
 * @date 2024/3/2 16:48
 */
@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ClientService clientService;
    @Autowired
    private LogService logService;

    //防止clientService注入失败
    private static ServerHandler serverHandler;

    @PostConstruct
    public void init(){
        serverHandler = this ;
        serverHandler.clientService = this.clientService;
        serverHandler.logService = this.logService;
    }

    //在所有ServerHandler中共享当前在线的授权信息
    private static Map<String,Integer> clients = new HashMap<>();

    //统一管理客户端channel和remote channel
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //所有remote channel共享的线程池，减少线程创建
    //bossGroup指的是所有指定端口的监听处理线程
    //workerGroup指的是所有端口所收到连接的处理线程

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private  EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootStrapHelper remoteHelper = new ServerBootStrapHelper();

    //客户端标识clientKey
    private  String clientKey;

    //代理客户端的ChannelHandlerContext
    private ChannelHandlerContext ctx;
    //判断代理客户端是否已注册授权
    private boolean isRegister = false;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
        System.out.println(this.getClass()+"\r\n 有客户端建立连接，客户端地址为："+ctx.channel().remoteAddress());
    }

    //数据读取与转发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMessage message=(ProxyMessage) msg;
        if(message.getType() == ProxyMessage.type_register){
            processRegister(message);
        }
        else if (isRegister){
            switch (message.getType()){
                //客户端请求断开连接
                case ProxyMessage.type_disconnect:
                    processDisconnect(message);
                    break;
                case ProxyMessage.type_keeplive:
                    break;
                case ProxyMessage.type_data:
                    processData(message);
                    break;
                default:
                    System.out.printf("你干嘛?");
        }
    }else {
            System.out.println(this.getClass()+"\r\n 有未授权的客户端尝试发送消息，断开连接");
            ctx.close();
        }
    }

    //�����ж�


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        ctx.channel().close();
        //移除断开客户端的授权码
        clients.remove(clientKey);
        //移除断开客户端的授权码
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println(this.getClass()+"\r\n 客户端连接中断："+ctx.channel().remoteAddress());
    }
    //连接异常处理


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println(this.getClass()+"\r\n 连接异常，已中断");
        cause.printStackTrace();
    }

    /**
     * @Description 判断客户端是否有授权
     * @param [clientKey]
     * **/
    public synchronized boolean isLegal(String clientKey){
        boolean flag = serverHandler.clientService.checkClientKey(clientKey);
        if (flag){
            if (isExist(clientKey)){
                System.out.println("授权码重复登录\n");
                return false;
            }
            clients.put(clientKey,1);
            this.clientKey = clientKey;
            return true;
        }
        return false;
    }
    /**
     * @Description 判断授权码是否已存在连接
     * **/
    public boolean isExist(String clientKey){
        if (clients.get(clientKey)!=null)return true;
        return false;
    }

    /**
     * @Description 处理客户端注册请求
     * @Param [message]
     * @return void
     **/
    public void processRegister(ProxyMessage message)throws Exception{
        HashMap<String,Object> metaData = new HashMap<>();
        ServerHandler serverHandler =this;
        String clientKey = message.getMetaData().get("clientKey").toString();
        if (isLegal(clientKey)){
            String host = (String) ConfigParser.get("server-host");
            ArrayList<Integer> ports = (ArrayList<Integer>) message.getMetaData().get("ports");
            try {
                for (int port:ports){
                    ChannelInitializer channelInitializer = new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            RemoteHandler remoteHandler = new RemoteHandler();
                            remoteHandler.setValue(serverHandler,port,clientKey);
                            channel.pipeline().addLast(
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    remoteHandler
                            );
                            //向channelGroup注册remote channel
                            channels.add(channel);
                        }
                    };
                    remoteHelper.bootStart(bossGroup,workerGroup,host,port,channelInitializer);
                }
                metaData.put("isSuccess",true);
                isRegister = true;
                System.out.println(this.getClass()+"\r\n 客户端注册成功，clientKey为："+clientKey);
            }
            catch (Exception e){
                metaData.put("isSuccess",false);
                metaData.put("reason",e.getMessage());
                System.out.println(this.getClass()+"\r\n processRegister()中出现错误");
                System.out.println(this.getClass()+"\r\n 启动器出错，客户端注册失败，clientKey为："+clientKey);
                e.printStackTrace();
            }
        }else {
            metaData.put("isSuccess",false);
            metaData.put("reason","client-key不对哦");
            System.out.println(this.getClass()+"\r\n 客户端注册失败，使用了不合法的clientKey，clientKey为："+clientKey);
        }
        ProxyMessage proxyMessage = new ProxyMessage();
        proxyMessage.setType(ProxyMessage.type_auth);
        proxyMessage.setMetaData(metaData);
        ctx.writeAndFlush(proxyMessage);
    }
    /**
     * @Description 处理客户端断开请求
     * @Param [message]
     * @return void
     **/
    public void processDisconnect(ProxyMessage pmsg){
        channels.close(new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                return channel.id().asLongText().equals(pmsg.getMetaData().get("channelId"));
            }
        });
        System.out.println(this.getClass()+"\r\n 有客户端请求断开，clientKey为："+clientKey);
    }

    /**
     * @Description 处理客户端发送的数据
     * @Param [message]
     * @return void
     **/
    
    public void processData(ProxyMessage proxyMessage){
        if (proxyMessage.getData()==null || !isExist(proxyMessage.getMetaData().get("clientKey").toString())){
            return;
        }
        //根据channelId转发到channelGroup上注册的相应remote channel(外部请求)
        channels.writeAndFlush(proxyMessage.getData(), new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                if (channel.id().asLongText().equals(proxyMessage.getMetaData().get("channelId"))){
                    //避免强转发生错误导致ClassCastException
                    Object clientKeyObj = proxyMessage.getMetaData().get("clientKey");
                    if (clientKeyObj != null) {
                        String clientKey = clientKeyObj.toString();
                        serverHandler.logService.addLog(clientKey, ((InetSocketAddress)channel.localAddress()).getPort(), (double) proxyMessage.getData().length/1024);
                        return true;
                    }
                }
                return false;
            }
        });
        System.out.println(this.getClass()+"\r\n 收到客户端返回数据，数据量为"+proxyMessage.getData().length+"字节");
    }
}
