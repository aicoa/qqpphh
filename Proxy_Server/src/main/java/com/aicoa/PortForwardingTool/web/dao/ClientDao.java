package com.aicoa.PortForwardingTool.web.dao;

import org.apache.ibatis.annotations.Param;
import com.aicoa.PortForwardingTool.web.entity.Client;

import java.util.List;

/**
 * @author aicoa
 * @date 2024/3/3 14:41
 */
public interface ClientDao {
    /**
     * @Description
     * @Param client
     * */
    int addClient(@Param("client") Client Client);

    int deleteClient(@Param("id")int id);

    /**
     * @Description 查询所有客户
     * @Param []
     **/
    List<Client> getClients();

    Client checkClientKey(@Param("clientKey")String clientKey);
}
