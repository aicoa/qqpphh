package com.aicoa.PortForwardingTool.web.controller;

/**
 * @author aicoa
 * @date 2024/3/3 16:40
 */

import com.aicoa.PortForwardingTool.server.ServerRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * @Description server�������
 * @Author aicoa
 */
@Controller
public class ServerController {
    @Autowired
    private ServerRun serverRun;
    private static ServerController serverController;
    @PostConstruct
    public void init(){
        serverController = this;
        serverController.serverRun = this.serverRun;
        System.out.println("初始化完成");
        try {
            System.out.println("原神,启动！");
            serverController.serverRun.start();
        }
        catch (Exception e){
            System.out.println("寄");
            e.printStackTrace();
        }
    }
}
