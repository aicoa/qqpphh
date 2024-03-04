package com.aicoa.PortForwardingTool.web.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.aicoa.PortForwardingTool.web.entity.Log;

import java.util.List;

/**
 * @author aicoa
 * @date 2024/3/3 14:46
 */
@Repository
public interface LogDao {
    /**
     * @Description 获取所有日志，按照授权码，端口，日期分类
     * @Param []
     **/
    List<Log> getLogs();
    int addLog(@Param("clientKey") String clinetKey,@Param("port") int Port,@Param("flow")double flow);

}
