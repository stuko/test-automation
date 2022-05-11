package com.auto.test.jmeter.plugin.common.run.executor;

import com.google.gson.Gson;
import org.apache.jmeter.threads.JMeterContextService;
import java.util.Map;

public abstract class AbstractPluginExecutor implements  TestPluginExecutor{
    Map<String,Object> configMap;
    Gson gson = new Gson();

    public Map<String, Object> getConfigMap() {
        return configMap;
    }
    public void setConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    public Map<String,Object> writeRequestToJMeterContext(String json){
        Map<String,Object> map = gson.fromJson(json, Map.class);
        map.forEach((k,v)->{
            JMeterContextService.getContext().getVariables().put(k, v.toString());
        });
        return map;
    }

    public String toResponseJson(Map<String,Object> map){
        return new Gson().toJson(map);
    }
}
