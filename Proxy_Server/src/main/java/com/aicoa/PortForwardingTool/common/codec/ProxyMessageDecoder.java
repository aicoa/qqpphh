package com.aicoa.PortForwardingTool.common.codec;

import com.aicoa.PortForwardingTool.common.protocol.ProxyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author aicoa
 * @date 2024/3/2 15:11
 */
public class ProxyMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int type = byteBuf.readInt(); //����
        int metaDataLength = byteBuf.readInt();
        CharSequence metaDataString = byteBuf.readCharSequence(metaDataLength, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(metaDataString.toString());
        Map<String, Object> metaData = jsonObject.toMap();

        byte[] data = null;
        if (byteBuf.isReadable()) {
            data = ByteBufUtil.getBytes(byteBuf);
        }

        ProxyMessage pm = new ProxyMessage();
        pm.setType(type);
        pm.setMetaData(metaData);
        pm.setData(data);

        list.add(pm);
    }
}
