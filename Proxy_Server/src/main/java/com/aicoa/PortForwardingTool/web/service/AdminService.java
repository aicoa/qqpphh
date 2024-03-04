package com.aicoa.PortForwardingTool.web.service;

import com.aicoa.PortForwardingTool.web.dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author aicoa
 * @date 2024/3/3 14:52
 */
@Service
public class AdminService {
    @Autowired
    private AdminDao adminDao;

    /**
     * @Description 管理员登录
     * @Param admin
     * @return boolean
     * **/
    public boolean login(String username,String passwd){
        String md5Pass = DigestUtils.md5DigestAsHex(passwd.getBytes());
        return adminDao.login(username,md5Pass) != null;
    }

    /**
     * @Description 修改密码
     * @Param [username,oldPass,newPass]
     * @return boolean
     * **/
    public boolean changePass(String username,String oldPasswd,String newPasswd){
        String md5OldPass = DigestUtils.md5DigestAsHex(oldPasswd.getBytes());
        String md5NewPass = DigestUtils.md5DigestAsHex(newPasswd.getBytes());
        return adminDao.changePass(username,md5OldPass,md5NewPass)>0;
    }
}
