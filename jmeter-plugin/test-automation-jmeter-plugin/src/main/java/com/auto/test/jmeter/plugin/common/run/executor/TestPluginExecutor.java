package com.auto.test.jmeter.plugin.common.run.executor;


import java.util.Map;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponse;

public interface TestPluginExecutor {
    void init(TestPluginTestData data , TestPluginCallBack callBack);
    TestPluginResponse execute();
    TestPluginTestData getTestData();
    Map<String,Object> getConfigMap();
    void setConfigMap(Map<String,Object> config);
    void setTestData(TestPluginTestData testData);
    void stop();
    void start();
    Map<String,Object> writeRequestToJMeterContext(String json);
    String toResponseJson(Map<String,Object> map);
}
