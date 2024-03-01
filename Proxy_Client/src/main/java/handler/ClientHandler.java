package handler;

import com.config.ConfigParser;
import com.protocol.ProxyMessage;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import handler.LocalHandler;
import helper.ClientBootStrapHelper;
/**
 * @author aicoa
 * @date 2024/3/1 0:41
 * @difficut 这儿有几个难点，一个是channelread的复杂性；Channel 和 EventLoopGroup，是另一个潜在的难点。确保所有资源在它们不再需要时被适当地关闭和释放
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    //对channels全局管理
    private ChannelGroup channels =new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //ServerPort 与LocalPort映射map
    private ConcurrentHashMap<Integer,Integer> portMap = new ConcurrentHashMap<>();
    //所有localChannel共享，减少线程的切换,可以提供多种服务，所以是并发的
    private EventLoopGroup localGroup = new NioEventLoopGroup();
    //每个外部请求channelid与其对应处理器handler的映射关系
    private ConcurrentHashMap<String,LocalHandler> localHandlerMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx =null;
    public  ChannelHandlerContext getCtx(){return ctx;}

    //建立连接，初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx =ctx;
        //连接建立，请求注册
        ProxyMessage ms =new ProxyMessage();
        ms.setType(ProxyMessage.type_register);
        HashMap<String,Object> metaData =new HashMap<>();
        metaData.put("clientKey", ConfigParser.get("client-key"));
        //获取配置中的服务器端口
        ArrayList<Integer> ServerPortArr =new ArrayList<>();
        for(Map<String,Object> item: ConfigParser.getPortArr()){
            ServerPortArr.add((Integer) item.get("server-port"));
            //保存映射关系
            portMap.put((Integer) item.get("server-port"),(Integer) item.get("client-port"));
        }
        metaData.put("ports",ServerPortArr);
        ms.setMetaData(metaData);
        ctx.writeAndFlush(ms);
        System.out.println(this.getClass()+"\r\n 与服务器连接成功，注册中...");
    }

    //读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMessage ms =(ProxyMessage) msg;
        switch (ms.getType()){
            //授权
            case ProxyMessage.type_auth:
                processAuth(ms);
                break;
            //外部入，与内网建立连接；
            case ProxyMessage.type_connect:
                processConnect(ms);
                break;
            //断开连接
            case ProxyMessage.type_disconnect:
                processDisConnect(ms);
                break;
            case ProxyMessage.type_keeplive:
                break;
            case ProxyMessage.type_data:
                processData(ms);
                break;
        }
    }

    //重写连接中断

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            // 关闭所有channel
            channels.close();

            // 向用户显示断开连接的信息
            String message = this.getClass().getName() + "\r\n 与服务器连接断开";
            System.out.println(message); // 控制台输出，用户可见
            // 优雅地关闭本地group
            localGroup.shutdownGracefully();

        } catch (Exception e) {
            System.err.println("Error occurred during channel inactive handling"); // 错误信息输出到控制台
        } finally {
            // 调用父类处理，确保所有必要的Netty内部清理都被执行
            super.channelInactive(ctx);
        }
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(this.getClass()+"\r\n 连接异常");
        cause.printStackTrace();
        //传递异常
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }
    /**
     * @Description 授权处理
    **/
    public void processAuth(ProxyMessage proxyMessage){
        if ((Boolean) proxyMessage.getMetaData().get("isSuccess")){
            System.out.println(this.getClass()+"\r\n 注册成功");
        }
        else{
            ctx.fireExceptionCaught(new Throwable());
            ctx.channel().close();
            System.out.println(this.getClass()+"\r\n 注册失败，原因："+proxyMessage.getMetaData().get("reason"));
        }
    }

    /**
     * @Description 连接建立
     **/
    public void processConnect(ProxyMessage proxyMessage){
        ClientHandler clientHandler =this;
        ClientBootStrapHelper localHelper = new ClientBootStrapHelper();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
            LocalHandler localHandler = new LocalHandler(clientHandler,proxyMessage.getMetaData().get("channelId").toString());

            //向pipline中添加handler，来保证处理方法和顺序
            channel.pipeline().addLast(
                new ByteArrayEncoder(),
                new ByteArrayDecoder(),
                localHandler
            );
            channels.add(channel);
                localHandlerMap.put(proxyMessage.getMetaData().get("channelId").toString(),localHandler);
            }
        };
        String localhost =(String) ConfigParser.get("local-host");
        //根据portMap将远程服务端口作为key获取对应的本地端口
        int remotePort = (Integer) proxyMessage.getMetaData().get("remotePort");
        int localPort = portMap.get(remotePort);
        localHelper.start(localGroup,channelInitializer,localhost,localPort);
        System.out.println(this.getClass()+"\r\n 服务器"+remotePort+"端口进入连接，正在向本地"+localPort+"端口建立连接");
    }

    /**
     * @Description 处理断开
     * **/
    public void processDisConnect(ProxyMessage proxyMessage){
        String channelID = proxyMessage.getMetaData().get("channelId").toString();
        LocalHandler handler =localHandlerMap.get(channelID);
        if (handler !=null){
            handler.getLocalctx().close();
            localHandlerMap.remove(channelID);
        }
    }

    public void processData(ProxyMessage msg){
        if (msg.getData()==null || msg.getData().length<=0) return;

        String channelID = msg.getMetaData().get("channelId").toString();
        LocalHandler localHandler=localHandlerMap.get(channelID);

        if (localHandler != null) localHandler.getLocalctx().writeAndFlush(msg.getData());

        System.out.println(this.getClass()+"\r\n 收到服务器数据，数据量为"+msg.getData().length+"字节");

    }






    }


