package com.kcb.fds.jmeter.plugin.common.run.executor;

import java.util.Map;

public abstract class AbstractPluginExecutor implements  TestPluginExecutor{
    Map<String,Object> configMap;

    public Map<String, Object> getConfigMap() {
        return configMap;
    }
    public void setConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
    }
}
