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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractPluginExecutor implements  TestPluginExecutor{

    Logger logger = LoggerFactory.getLogger(AbstractPluginExecutor.class);
    Map<String,Object> configMap;
    Gson gson = new Gson();
    ExecutorMap.ExecutorType type = ExecutorMap.ExecutorType.DEFAULT;

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
            logger.info("########### Executor init : MessageCombination");
            if(this.getTestMessage() == null) {
                this.setTestMessage(new TestMessageByCombination());
            }
            logger.info("########### Executor init : TestPlugin 객체를 할당 받음");
            if(data != null) {
                this.setTestData(data);
                // 테스트 메타 정보를 가지고 테스트 데이터를 생성해 주는 객체 TestMessage
                this.getTestMessage().build(this.getTestData().getData());
            }
            if(this.getTestData().getTestDatas() == null) {
                // FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).removeAll();
                this.getTestData().setTestDatas(FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path));
            }

            if(this.is_stop()){
                try {
                    // TestMessage 에서 생성한 테스트 데이터들을 CallBack 을 통해 사용하도록 함.
                    this.getExecutorService().submit(new Thread(){
                        public void run(){
                            getTestMessage().getFileMessage(callBack);
                        }
                    });
                }catch(Exception e){
                    callBack.call("Exception : " + e.toString(), 0);
                }
            }
            // this.getTestMessage().setStop(false);
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    @Override
    public TestPluginResponse execute() {
        String data = this.getTestData().next();
        // String data = FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).next();
        logger.info("Default #################");
        logger.info("Message : {} by {}" , data , this.getName());
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
                response.setError(true);
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
            response.setError(true);
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
    
    // 테스트 데이터 메타(testdata) 와  물리적 테스트 데이터(testdatas) 를 가지고 있는 객체
    @Override
    public void setTestData(TestPluginTestData testData) {
        this.testData = testData;
    }

    @Override
    public void stop(){
        this.getTestMessage().setStop(true);
        try {
            if (this.getExecutorService() != null){
                this.getExecutorService().shutdownNow();
                // 재생성 해줌.
                // 그냥 재사용시 RejectedExecutionException 발생
                executors = Executors.newFixedThreadPool(10);
            }
            this.getTestData().setData( null );
            this.getTestData().setTestDatas( null );
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    @Override
    public void start(){
        if(this.getTestData().getTestDatas() == null || this.getTestData().getTestDatas().size() == 0) {
            this.init(this.getTestData(), (d, cnt) -> {
                logger.info("Test Data is writed...{}", d);
                getTestData().getTestDatas().write(d);
                return null;
            });
            this.getTestMessage().setStop(false);
        }
    }
    @Override
    public void start(TestPluginCallBack callback){
        if(this.getTestData().getTestDatas() == null || this.getTestData().getTestDatas().size() == 0) {
            this.init(this.getTestData(), (d, cnt) -> {
                logger.info("Test Data with Callback is writed...{}", d);
                getTestData().getTestDatas().write(d);
                callback.call(d, cnt);
                return null;
            });
            this.getTestMessage().setStop(false);
        }
    }

    @Override
    public ExecutorService getExecutorService(){
        return this.executors;
    }
    @Override
    public void setExecutorService(ExecutorService svc){
        this.executors = svc;
    }

    public ExecutorMap.ExecutorType getTYPE(){
       return type;
    }

    public void setTYPE(ExecutorMap.ExecutorType type){
        this.type = type;
    }
}

