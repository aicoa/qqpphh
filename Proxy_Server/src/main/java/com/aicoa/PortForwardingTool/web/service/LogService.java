package com.aicoa.PortForwardingTool.web.service;

import com.aicoa.PortForwardingTool.web.dao.LogDao;
import com.aicoa.PortForwardingTool.web.entity.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author aicoa
 * @date 2024/3/3 15:22
 */
@Service
public class LogService {
        @Autowired
        private LogDao logDao;

        /**
         * @Description 获取日志，按照授权码，端口，日期分类
         * @Date 17:26 2020/4/15
         * @Param []
         **/
        public List<Log> getLogs(){
            return logDao.getLogs();
        }

        /**
         * @Description 插入日志
         * @Date 13:23 2020/4/16
         * @Param [clientKey, port, flow]
         * @return boolean
         **/
        public boolean addLog(String clientKey,int port,double flow) {
            return logDao.addLog(clientKey, port, flow) > 0;
        }

    }
