/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kcb.fds.plugin.test.result.data;
import com.google.gson.Gson;
import java.util.Map;
/**
 *
 * @author O218001_D
 */
public class TestResultConfiguration {
    
    public static enum ConfigType  { SQL,KAFKA,FILE,REST };
    ConfigType type;
    Map<String,Object> configMap;

    public Map<String, Object> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }
    
    public void load(String config_string){
        
        Gson gson = new Gson();
        Map<String,Object> map = gson.fromJson(config_string, Map.class);
        if(this.getType().equals(ConfigType.SQL)){
            
            
        }else if(this.getType().equals(ConfigType.KAFKA)){
            
            
        }else if(this.getType().equals(ConfigType.FILE)){
            
            
        }else if(this.getType().equals(ConfigType.REST)){
            
            
        }else {
            this.setType(ConfigType.SQL);
        }
    }
    
}
