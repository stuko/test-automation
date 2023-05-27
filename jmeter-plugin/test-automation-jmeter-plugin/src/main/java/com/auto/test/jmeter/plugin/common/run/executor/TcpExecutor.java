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

    String ip;
    String port;
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    String name = "TcpExecutor";
    @Override
    public void init(TestPluginTestData data , TestPluginCallBack callBack) {
        try {
            super.init(data,callBack);
            if(this.getConfigMap() != null) {
                ip = this.getConfigMap().get("ip") + "";
                port = this.getConfigMap().get("port") + "";
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    @Override
    public TestPluginResponse execute() {
        // String data = FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).next();
        String data = this.getTestData().next();
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
                // logger.error(e.toString(), e);
                response.setResponse("Fail : " + e.toString());
                response.setError(true);
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
            // logger.error(e.toString(), e);
            response.setResponse("Fail : " + e.toString());
            response.setError(true);
        } finally {

        }
        return response;
    }

}