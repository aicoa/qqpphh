package com.aicoa.PortForwardingTool.web.controller;

/**
 * @author aicoa
 * @date 2024/3/2 21:28
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.aicoa.PortForwardingTool.web.service.AdminService;

import javax.servlet.http.HttpServletRequest;
@RequestMapping
@RestController
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * @Description 管理员登录
     * @Param [username,password]
     * **/
    @RequestMapping("/login")
    public boolean login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request){
        boolean flag = adminService.login(username,password);
        if (flag) request.getSession().setAttribute("user",username);
        return  flag;
    }
    /**
     * @Description 修改密码
     * @Param [username,oldPass,newPass]
     * @return boolen
     * **/
    @RequestMapping("/changPass")
    public boolean changePass(@RequestParam("username") String username,
                              @RequestParam("oldPass") String oldPass,
                              @RequestParam("newPass") String newPass){
        return adminService.changePass(username,oldPass,newPass);
    }

    //检查登陆状态
    @RequestMapping("/isLogin")
    public String isLogin(HttpServletRequest request){
        Object user = request.getSession().getAttribute("user");
        if (user!=null){
            return (String) user;
        }
        return null;
    }
}
