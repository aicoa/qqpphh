package com.aicoa.PortForwardingTool.web.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.aicoa.PortForwardingTool.web.entity.Admin;

/**
 * @author aicoa
 * @date 2024/3/3 14:37
 */
@Repository
public interface AdminDao {
    /**
     * @Description 登录
     * @Param [username,passwd]
     * **/
    Admin login(@Param("username") String username,@Param("passwd") String passwd);

    /**
     * @Description 修改密码
     * @Param [username,oldPass,newPass]
     * **/
    public int changePass(@Param("username") String username,@Param("oldPass") String oldPass,@Param("newPass") String newPss );

}
