package com.aicoa.PortForwardingTool.web.service;

import com.aicoa.PortForwardingTool.web.dao.ClientDao;
import com.aicoa.PortForwardingTool.web.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author aicoa
 * @date 2024/3/3 15:21
 */
    @Service
    public class ClientService {

        @Autowired
        private ClientDao clientDao;

        /**
         * @Description 添加用户
         * @Param [client]
         * @return boolean
         **/
        public boolean addClient(Client client){
            return clientDao.addClient(client)>0;
        }

        /**
         * @Description 删除用户
         * @Param [id]
         * @return boolean
         **/
        public boolean deleteClient(int id){
            return clientDao.deleteClient(id)>0;
        }

        /**
         * @Description 查询所有用户
         * @Param []
         **/
        public List<Client> getClients(){
            return clientDao.getClients();
        }

        /**
         * @Description 检查clientKey
         * @Param [clientKey]
         * @return boolean
         **/
        public boolean checkClientKey(String clientKey){
            return clientDao.checkClientKey(clientKey)!=null;
        }

    }
