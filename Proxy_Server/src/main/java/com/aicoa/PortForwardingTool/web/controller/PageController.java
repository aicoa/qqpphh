package com.aicoa.PortForwardingTool.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author aicoa
 * @date 2024/3/3 16:36
 */
@Controller
@RequestMapping("/PFT")
public class PageController {
    @RequestMapping("/admin")
    public String goAdmin() {return"admin";}

    @RequestMapping("/index")
    public String goIndex() {return"index";}

    @RequestMapping("/client")
    public String goClient() {return"client";}

    @RequestMapping("/login")
    public String goLogin() {return"login";}

    @RequestMapping("/log")
    public String goLog() {return"log";}


}
