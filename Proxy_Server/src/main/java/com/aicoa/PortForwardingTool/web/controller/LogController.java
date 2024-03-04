package com.aicoa.PortForwardingTool.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aicoa.PortForwardingTool.web.service.LogService;
import com.aicoa.PortForwardingTool.web.entity.Log;

import java.util.List;

/**
 * @author aicoa
 * @date 2024/3/3 16:27
 */
@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

    @Autowired
    private  LogService  logservice;
    /**
     * @Description 获取日志，按照授权码，端口，日期分类
     * @Param []
     * @return java.util.List<com.aicoa.PortForwardingTool.web.entity.Log>
     **/
    @RequestMapping("/get")
    public List<Log>  getLogs(){return logservice.getLogs();}

}
