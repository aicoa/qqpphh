package com.aicoa.PortForwardingTool;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author aicoa
 * @date 2024/3/3 15:26
 */
@MapperScan("com.aicoa.PortForwardingTool.web.dao")
@SpringBootApplication
public class ProxyServerApplication {
    public static void main(String[] args){ SpringApplication.run(ProxyServerApplication.class, args);}
}
