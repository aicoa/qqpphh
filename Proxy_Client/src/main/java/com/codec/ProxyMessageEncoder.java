package com.codec;

import com.protocol.ProxyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author aicoa
 * @date 2024/2/29 23:30
 */
public class ProxyMessageEncoder extends MessageToByteEncoder<ProxyMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProxyMessage proxyMessage, ByteBuf byteBuf) throws Exception {
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        DataOutputStream dos =new DataOutputStream(baos);

        int type = proxyMessage.getType();
        dos.writeInt(type);

        JSONObject metaDataJson =new JSONObject(proxyMessage.getMetaData());
        byte[] metaDataBytes =metaDataJson.toString().getBytes(CharsetUtil.UTF_8);
        dos.writeInt(metaDataBytes.length);
        dos.write(metaDataBytes);

        if(proxyMessage.getData()!=null && proxyMessage.getData().length>0){dos.write(proxyMessage.getData());}

        byte[] data = baos.toByteArray();
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
