package com.aicoa.PortForwardingTool.common.config;

import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.nio.file.Paths;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author aicoa
 * @date 2024/3/2 15:27
 * 这里被注释掉是因为测试出现了一定的问题，mybatis在通过jar的形式启动时找不到resource目录中的内容，这里我图方便就删除了jar与yaml在同一目录下启动的形式
 * 直接将所有配置放在resource目录下
 */
//public class ConfigParser {
//    private static Map<String ,Object> config =null;
//    private static ArrayList<Map<String,Object>> portArrList=null;

//    public ConfigParser(){
//        try {
//           //IDEA中运行
//            File file = getProjectConfigFile();
//            //打包到服务器
//            //File file = getServerConfigFile();
//            InputStream inputStream = new FileInputStream(file);
//
//            Yaml yaml = new Yaml();
//            config =(Map<String, Object>) yaml.load(inputStream);
//            portArrList = (ArrayList<Map<String, Object>>) get("config");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    public File getProjectConfigFile() throws Exception{
//        return ResourceUtils.getFile("classpath:config/server.yaml");
//    }
//
//
//    public File getServerConfigFile(){
//        String ClassPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        ClassPath = ClassPath.substring(5,ClassPath.indexOf("ProxyServer.jar"))+"server.yaml";
//        return new File(ClassPath);
//    }
//
//    public static Object get(String key){
//        if (config == null) new ConfigParser();
//        return config.get(key);
//    }
//    public static ArrayList<Map<String,Object>> getPortArr(){
//        if (portArrList == null) new ConfigParser();
//        return portArrList;
//    }
//
//}
public class ConfigParser {
    private static Map<String, Object> config = null;
    private static ArrayList<Map<String, Object>> portArrList = null;

    public ConfigParser() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/server.yaml");
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found! config/server.yaml");
            }

            Yaml yaml = new Yaml();
            config = (Map<String, Object>) yaml.load(inputStream);
            portArrList = (ArrayList<Map<String, Object>>) get("config");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object get(String key) {
        if (config == null) new ConfigParser();
        return config.get(key);
    }

    public static ArrayList<Map<String, Object>> getPortArr() {
        if (portArrList == null) new ConfigParser();
        return portArrList;
    }
}