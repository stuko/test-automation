package com.auto.test.jmeter.plugin.common.run.executor;

import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.auto.test.jmeter.plugin.common.data.TestMessageByCombination;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.auto.test.jmeter.plugin.common.run.message.TestMessage;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponse;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponseImpl;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.google.gson.Gson;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractPluginExecutor implements  TestPluginExecutor{

    Logger logger = LoggerFactory.getLogger(AbstractPluginExecutor.class);
    Map<String,Object> configMap;
    Gson gson = new Gson();

    TestMessage message = new TestMessageByCombination();
    ExecutorService executors = Executors.newFixedThreadPool(10);
    TestPluginTestData testData;

    public Map<String, Object> getConfigMap() {
        return configMap;
    }
    public void setConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    @Override
    public void init(TestPluginTestData data , TestPluginCallBack callBack) {
        try {
            this.setTestMessage(new TestMessageByCombination());
            this.getTestMessage().setStop(false);
            this.setTestData(data);
            this.getTestMessage().build(this.getTestData().getData());
            try {
                this.getExecutorService().submit(new Thread(){
                    public void run(){
                        getTestMessage().getFileMessage(callBack);
                    }
                });
            }catch(Exception e){
                callBack.call("Exception : " + e.toString(), 0);
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    @Override
    public TestPluginResponse execute() {
        String data = FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).next();
        logger.info("Default #################");
        logger.info("Message : {} " , data);
        logger.info("Default #################");
        if(data == null){
            logger.info("No more teat data !!!!!!!!!!!!!!!");
            TestPluginResponseImpl response = new TestPluginResponseImpl();
            try {
                response.setRequest("No more data");
                response.setSize("No more data".getBytes().length);
                response.setResponse("Success");
            } catch (Exception e) {
                logger.error(e.toString(), e);
                response.setResponse("Fail : " + e.toString());
            }
            return response;
        }

        TestPluginResponseImpl response = new TestPluginResponseImpl();
        try {
            response.setRequest(data);
            response.setSize(data.getBytes().length);
            response.setResponse("Success");
        } catch (Exception e) {
            // exception
            logger.error(e.toString(), e);
            response.setResponse("Fail : " + e.toString());
        }
        return response;
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

    @Override
    public boolean is_start() {
        return !message.isStop();
    }

    @Override
    public boolean is_stop() {
        return message.isStop();
    }
    @Override
    public TestMessage getTestMessage(){
        return this.message;
    }
    @Override
    public void setTestMessage(TestMessage msg){
        this.message = msg;
    }

    @Override
    public TestPluginTestData getTestData() {
        return testData;
    }

    @Override
    public void setTestData(TestPluginTestData testData) {
        this.testData = testData;
    }

    @Override
    public void stop(){
        this.getTestMessage().setStop(true);
    }

    @Override
    public void start(){
        this.init(this.getTestData(), (d, cnt) -> {
            FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).write(d);
            return null;
        });
        this.getTestMessage().setStop(false);
    }
    @Override
    public void start(TestPluginCallBack callback){
        this.init(this.getTestData(), (d, cnt) -> {
            FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).write(d);
            callback.call(d,cnt);
            return null;
        });
        this.getTestMessage().setStop(false);
    }

    @Override
    public ExecutorService getExecutorService(){
        return this.executors;
    }
    @Override
    public void setExecutorService(ExecutorService svc){
        this.executors = svc;
    }
}
