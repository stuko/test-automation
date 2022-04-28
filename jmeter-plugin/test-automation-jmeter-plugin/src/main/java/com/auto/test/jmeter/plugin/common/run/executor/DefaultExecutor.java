package com.auto.test.jmeter.plugin.common.run.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.auto.test.jmeter.plugin.common.data.TestMessageByCombination;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponse;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponseImpl;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;

public class DefaultExecutor  extends AbstractPluginExecutor {

    Logger logger = LoggerFactory.getLogger(DefaultExecutor.class);

    TestMessageByCombination message = new TestMessageByCombination();
    ExecutorService executors = Executors.newFixedThreadPool(10);
    TestPluginTestData testData;

    @Override
    public void stop(){
        message.setStop(true);
    }

    @Override
    public void start(){
        message.setStop(false);
    }

    @Override
    public void init(TestPluginTestData data , TestPluginCallBack callBack) {
        // 초기 설정
        try {
            message = new TestMessageByCombination();
            message.setStop(false);
            this.testData = data;
            
            // 테스트 데이터 생성
            message.build(testData.getData());
            try {
                executors.submit(new Thread(){
                    public void run(){
                        message.getFileMessage(callBack);
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

    @Override
    public TestPluginTestData getTestData() {
        return testData;
    }

    @Override
    public void setTestData(TestPluginTestData testData) {
        this.testData = testData;
    }
}