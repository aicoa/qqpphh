package com.aicoa.PortForwardingTool.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author aicoa
 * @date 2024/3/3 0:50
 */
public class Log {
    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private String clientKey;
    private String clientName;
    private int port;
    private long flow;
    @JsonFormat(pattern = "yyyy-MMM-dd",timezone = "GMT")
    private Date date;

    @Override
    public String toString() {
        return "Log{" +
                "clientKey='" + clientKey + '\'' +
                ", clientName='" + clientName + '\'' +
                ", port=" + port +
                ", flow=" + flow +
                ", date=" + date +
                '}';
    }
}
