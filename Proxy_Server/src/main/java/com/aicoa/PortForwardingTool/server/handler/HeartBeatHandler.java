package com.aicoa.PortForwardingTool.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author aicoa
 * @date 2024/3/2 16:47
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private int waitCount = 1;
    private static final int Max_Wait_Count = 2;


    private static final long MAX_TIME_MILLIS_LIMIT = 30 * 1000;

    private long lastDisConnectTimeMillis = 0;

    private long currentMillis = 0;



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        currentMillis = System.currentTimeMillis();

        if ((currentMillis - lastDisConnectTimeMillis) > MAX_TIME_MILLIS_LIMIT) {
            if (waitCount == 1) {
                waitCount += 1;
                System.out.println(this.getClass() + "\r\n 首次超时");
            } else {
                System.out.println(this.getClass() + "\r\n 已重置超时次数与最后一次超时时间");
            }
            lastDisConnectTimeMillis = currentMillis;
        } else if (waitCount > Max_Wait_Count) {
            ctx.channel().close();
            System.out.println(this.getClass() + "\r\n 连续读超时次数达到3次，已主动断开与客户端的连接");
        } else {
            System.out.println(this.getClass()+"\r\n 读超时次数："+waitCount);
            waitCount += 1;
            lastDisConnectTimeMillis = currentMillis;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println(this.getClass()+"\r\n 客户端连接异常");
        cause.printStackTrace();
    }
}
