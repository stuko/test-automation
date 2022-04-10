package com.kcb.fds.jmeter.plugin.common.run.executor;


import java.util.Map;
import com.kcb.fds.jmeter.plugin.common.function.TestPluginCallBack;
import com.kcb.fds.jmeter.plugin.common.data.TestPluginTestData;
import com.kcb.fds.jmeter.plugin.common.sampler.TestPluginResponse;

public interface TestPluginExecutor {
    void init(TestPluginTestData data , TestPluginCallBack callBack);
    TestPluginResponse execute();
    TestPluginTestData getTestData();
    Map<String,Object> getConfigMap();
    void setConfigMap(Map<String,Object> config);
    void setTestData(TestPluginTestData testData);
    void stop();
    void start();
}
