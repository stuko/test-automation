package com.auto.test.jmeter.plugin.common.run.executor;


import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.run.message.TestMessage;
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
    void start(TestPluginCallBack callback);
    boolean is_start();
    boolean is_stop();
    Map<String,Object> writeRequestToJMeterContext(String json);
    String toResponseJson(Map<String,Object> map);
    TestMessage getTestMessage();
    void setTestMessage(TestMessage msg);
    ExecutorService getExecutorService();
    void setExecutorService(ExecutorService svc);
}
