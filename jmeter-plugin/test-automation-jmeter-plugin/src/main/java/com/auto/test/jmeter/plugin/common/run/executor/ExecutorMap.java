package com.auto.test.jmeter.plugin.common.run.executor;

import java.util.HashMap;
import java.util.Map;
 
public class ExecutorMap {
    public static enum ExecutorType {KAFKA,HTTP}
    public static ExecutorMap map;
    static{map = new ExecutorMap();}
    private Map<ExecutorType,AbstractPluginExecutor> cache = new HashMap<>();
    private ExecutorMap(){}
    public static ExecutorMap getInstance(){if(map == null) map = new ExecutorMap();return map;}
    public AbstractPluginExecutor getExecutor(ExecutorType type){
        if(this.cache.get(type) == null){
            if(type == ExecutorType.KAFKA){
                this.cache.put(type,new KafkaExecutor());
            }else if(type == ExecutorType.HTTP){
                this.cache.put(type,new HttpExecutor());
            }
        }
        return this.cache.get(type);
    }
}

