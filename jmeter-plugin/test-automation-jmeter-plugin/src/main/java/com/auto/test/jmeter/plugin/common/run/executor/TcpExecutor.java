package com.auto.test.jmeter.plugin.common.run.executor;

import com.google.gson.Gson;
import com.auto.test.jmeter.plugin.common.data.TestMessageByCombination;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponseImpl;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.auto.test.jmeter.plugin.common.util.HttpUtil;
import com.auto.test.jmeter.plugin.common.util.SecurityUtil;
import com.auto.test.jmeter.plugin.common.util.TcpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponse;

public class TcpExecutor  extends AbstractPluginExecutor  {

    Logger logger = LoggerFactory.getLogger(TcpExecutor.class);

    TestMessageByCombination message = new TestMessageByCombination();
    ExecutorService executors = Executors.newFixedThreadPool(2);
    TestPluginTestData testData;
    String ip;
    String port;

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
        // �ʱ� ����
        try {
            message = new TestMessageByCombination();
            message.setStop(false);
            this.testData = data;
            if(this.getConfigMap() != null) {
                ip = this.getConfigMap().get("ip") + "";
                port = this.getConfigMap().get("port") + "";
            }

            // �׽�Ʈ ������ ����
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
        logger.info("HTTP #################");
        logger.info("Message : {} " , data);
        logger.info("HTTP #################");
        if(this.ip == null || this.port == null || data == null){
            logger.info("No more teat data !!!!!!!!!!!!!!!");
            TestPluginResponseImpl response = new TestPluginResponseImpl();
            try {
                response.setRequest("No more data or No ip or No port : " + ip + "," + port);
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
            TcpUtil.send(ip,port,data);
            response.setRequest(data);
            response.setSize(data.getBytes().length);
            response.setResponse(data);
        } catch (Exception e) {
            // exception
            logger.error(e.toString(), e);
            response.setResponse("Fail : " + e.toString());
        } finally {

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