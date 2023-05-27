package com.auto.test.jmeter.plugin.common.run.executor;

import java.util.HashMap;
import java.util.Map;
 
public class ExecutorMap {
    public static enum ExecutorType {KAFKA,HTTP,DEFAULT,TCP}
    public static ExecutorMap map;
    static{map = new ExecutorMap();}
    private Map<ExecutorType,AbstractPluginExecutor> cache = new HashMap<>();
    private ExecutorMap(){}
    public static ExecutorMap getInstance(){if(map == null) map = new ExecutorMap();return map;}
    public AbstractPluginExecutor getExecutor(ExecutorType type){
        if(this.cache.get(type) == null){
            AbstractPluginExecutor abstractPluginExecutor = null;
            if(type == ExecutorType.KAFKA){
                abstractPluginExecutor = new KafkaExecutor();
                abstractPluginExecutor.setTYPE(ExecutorType.KAFKA);
                this.cache.put(type,abstractPluginExecutor);
            }else if(type == ExecutorType.HTTP){
                abstractPluginExecutor = new HttpExecutor();
                abstractPluginExecutor.setTYPE(ExecutorType.HTTP);
                this.cache.put(type,abstractPluginExecutor);
            }else if(type == ExecutorType.TCP){
                abstractPluginExecutor = new TcpExecutor();
                abstractPluginExecutor.setTYPE(ExecutorType.TCP);
                this.cache.put(type,abstractPluginExecutor);
            }else {
                abstractPluginExecutor = new DefaultExecutor();
                abstractPluginExecutor.setTYPE(ExecutorType.DEFAULT);
                this.cache.put(type,abstractPluginExecutor);
            }
        }
        return this.cache.get(type);
    }
}

