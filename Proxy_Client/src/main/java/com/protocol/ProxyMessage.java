package com.protocol;
import java.util.Arrays;
import java.util.Map;

/**
 * @author aicoa
 * @date 2024/2/28 0:44
 * 自定义消息
 */
public class ProxyMessage {
    private  int type;
    //元数据
    private Map<String,Object> metaData;

    private byte[] data;
    //注册动作
    public static final int type_register =1;
    //授权动作
    public static final int type_auth =2;
    //建立连接
    public static final int type_connect=3;
    //断开连接
    public static  final int type_disconnect=4;
    //心跳
    public static  final int type_keeplive =5;
    //传输数据
    public static  final int type_data=6;

    public ProxyMessage(){}

    public ProxyMessage(int type){this.type=type;}

    public int getType(){return type;}

    public void setType(int type){this.type=type;}

    public Map<String ,Object> getMetaData(){return metaData;}

    public void setMetaData(Map<String ,Object> metaData){this.metaData=metaData;}

    public byte[] getData(){return data;}

    public void setData(byte[] data){this.data=data;}

    @Override
    public String toString() {
        return "ProxyMesaage{" +
                "type=" + type +
                ", metaData=" + metaData +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
