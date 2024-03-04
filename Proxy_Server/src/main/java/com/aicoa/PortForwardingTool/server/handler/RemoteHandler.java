package com.aicoa.PortForwardingTool.server.handler;

import com.aicoa.PortForwardingTool.common.protocol.ProxyMessage;
import com.aicoa.PortForwardingTool.web.service.LogService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * @author aicoa
 * @date 2024/3/2 16:48
 */
@Component
public class RemoteHandler extends ChannelInboundHandlerAdapter {
    //以下代码避免自动注入为null
    private  static RemoteHandler remoteHandler;

    @Autowired
    private LogService logService;

    @PostConstruct
    public void init(){
        remoteHandler = this;
        remoteHandler.logService = this.logService;
    }
    private ServerHandler serverHandler = null;
    private int remotePort;
    private String clientKey;

    /**
     * @Description 不用构造器赋值，SpringBoot自动注入
     * @Param [serverHandler,remotePort,clientKey]
     * **/
    public void setValue(ServerHandler serverHandler,int remotePort,String clientKey){
        this.serverHandler=serverHandler;
        this.remotePort=remotePort;
        this.clientKey=clientKey;
    }
    /**
     * @Description 发送数据到内网客户端流程封装
     * @param [type,channelId]
     * @return void
     * **/
    public void send(int type,String channelId,byte[] data){
        if (serverHandler ==null){
            System.out.println(this.getClass()+"\r\n channel不存在");
            return;
        }
        ProxyMessage message = new ProxyMessage();
        message.setType(type);
        HashMap<String ,Object> metaData =new HashMap<>();
        //每个请求都是一个channel，每个channel有唯一id，
        // 将该id发送至客户端，客户端返回响应时携带此id便可知道响应需要返回给哪个请求
        metaData.put("channelId",channelId);
        metaData.put("remotePort",remotePort);
        message.setMetaData(metaData);
        if (data != null) message.setData(data);
        this.serverHandler.getCtx().writeAndFlush(message);
        if(remoteHandler != null && ProxyMessage.type_data==type){
            //记录日志，只记录数据传输请求
            remoteHandler.logService.addLog(clientKey,((InetSocketAddress)serverHandler.getCtx().channel().localAddress()).getPort(),(double) message.getData().length/1024);
        }
    }

    //���ӳ�ʼ��
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        send(ProxyMessage.type_connect,ctx.channel().id().asLongText(),null);
        System.out.println(this.getClass()+"\r\n"+remotePort+"端口有请求进入，channelId为："+ctx.channel().id().asLongText());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //从外部连接接收到的数据
        byte[] data = (byte[]) msg;
        //调用发送方法转发到客户端
        send(ProxyMessage.type_data,ctx.channel().id().asLongText(),data);
        System.out.println(this.getClass()+"\r\n"+remotePort+"端口收到请求数据，数据量为"+data.length+"Bytes");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        send(ProxyMessage.type_disconnect,ctx.channel().id().asLongText(),null);
        System.out.println(this.getClass()+"\r\n"+remotePort+"端口有请求离开，channelId为："+ctx.channel().id().asLongText());
    }


}
