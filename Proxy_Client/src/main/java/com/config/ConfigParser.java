package com.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
/**
 * @author aicoa
 * @date 2024/3/1 0:21
 * 文件解析器
 */
public class ConfigParser {
    private static Map<String, Object> config =null ;
    private static ArrayList<Map<String,Object>> portArr =null ;

    public ConfigParser(){
        try {
            InputStream in =this.getClass().getClassLoader().getResourceAsStream("client.yaml");
            if (in == null) {
                throw new FileNotFoundException("client.yaml' 未在resources目录找到");
            }
            Yaml yaml = new Yaml();
            config = (Map<String,Object>)yaml.load(in);
            portArr =(ArrayList<Map<String, Object>>) config.get("config");

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static  Object get(String key){
        if (config == null)   new ConfigParser();
        return  config.get(key);
    }

    public static  ArrayList<Map<String,Object>> getPortArr(){
        if (portArr==null) new ConfigParser();
        return  portArr;
    }
}
