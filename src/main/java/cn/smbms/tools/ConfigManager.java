package cn.smbms.tools;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//读取配置文件的工具类--单列模式
public class ConfigManager {
    //类加载时,自动进行初始化操作
    private static ConfigManager configManager;
    private static Properties properties;
    //私有构造器--读取数据库配置文件
    private ConfigManager(){
        properties = new Properties();
        String configFile = "database.properties";
        InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream(configFile);
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static class ConfigManagerHelper{
        private static final ConfigManager INSTANCE = new ConfigManager();
    }
    //饿汉模式
    public static ConfigManager getInstance(){
        configManager = ConfigManagerHelper.INSTANCE;
        return configManager;
    }
    public String getValue(String key){
        return  properties.getProperty(key);
    }
}
