package com.kcb.fds.jmeter.plugin.common.run.executor;

import com.google.gson.Gson;
import com.kcb.fds.jmeter.plugin.common.sampler.TestPluginResponseImpl;
import com.kcb.fds.jmeter.plugin.common.data.TestMessageByCombination;
import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginConstants;
import com.kcb.fds.jmeter.plugin.common.util.HttpUtil;
import com.kcb.fds.jmeter.plugin.common.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.kcb.fds.jmeter.plugin.common.function.TestPluginCallBack;
import com.kcb.fds.jmeter.plugin.common.data.TestPluginTestData;
import com.kcb.fds.jmeter.plugin.common.sampler.TestPluginResponse;

public class HttpExecutor extends AbstractPluginExecutor  {

    Logger logger = LoggerFactory.getLogger(HttpExecutor.class);

    TestMessageByCombination message = new TestMessageByCombination();
    ExecutorService executors = Executors.newFixedThreadPool(2);
    TestPluginTestData testData;
    String url;
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
            if(this.getConfigMap() != null) {
               url = this.getConfigMap().get("url") + "";
            }

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
        TestPluginResponseImpl response = new TestPluginResponseImpl();
        String data = FileJsonArrayListQueue.getInstance(TestPluginConstants.fds_data_path).next();
        logger.info("HTTP #################");
        logger.info("Message : {} " , data);
        logger.info("HTTP #################");
        if(this.url == null || data == null){
            logger.error("No more teat data !!!!!!!!!!!!!!! : url = {} , data = {}" , this.url, data);
            try {
                response.setRequest("No more data or No Url : " + url);
                response.setSize("No more data".getBytes().length);
                response.setResponse("Success");
            } catch (Exception e) {
                logger.error(e.toString(), e);
                response.setResponse("Fail : " + e.toString());
            }
            return response;
        }
        try {
            response.setRequest(data);
            response.setSize(data.getBytes().length);
            response.setResponse(data);
            // ---------------------------------------
            // Change to okHttp3
            // 20211204
            // 오픈소스로 변경
            // 20211209
            // Change to Jmeter's Apache Http Common
            // ---------------------------------------
            // response.setResponse(SecurityUtil.callRest(url, data));
            // ---------------------------------------
            // Change to Jmeter's Apache Http Common
            // 20211209
            // Jmeter에 오픈소스 추가 어려움.
            // ---------------------------------------

            HttpUtil.call(url,data,(body)->{
                logger.info("Success : {}" , body);
                // response.setResponse(body.toString());
            });

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