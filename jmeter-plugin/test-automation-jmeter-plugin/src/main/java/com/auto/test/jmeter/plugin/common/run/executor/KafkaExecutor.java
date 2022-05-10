package com.auto.test.jmeter.plugin.common.run.executor;

import com.google.gson.Gson;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponseImpl;
import com.auto.test.jmeter.plugin.common.data.TestMessageByCombination;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.sampler.TestPluginResponse;

public class KafkaExecutor extends AbstractPluginExecutor {

    Logger logger = LoggerFactory.getLogger(KafkaExecutor.class);

    KafkaProducer<String, String> producer;
    String topicName;
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
        try {
            message = new TestMessageByCombination();
            message.setStop(false);
            this.testData = data;
            if(this.getConfigMap() != null) {
                Properties properties = new Properties();
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getConfigMap().get("server") + "");
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                producer = new KafkaProducer<>(properties);
                topicName = this.getConfigMap().get("topic") + "";
            }

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
        logger.info("KAFKA #################");
        logger.info("Message : {} " , data);
        logger.info("KAFKA #################");
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
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, data);
        TestPluginResponseImpl response = new TestPluginResponseImpl();
        try {
            response.setRequest(data);
            response.setSize(data.getBytes().length);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    // some exception
                    logger.error(exception.toString(), exception);
                }
            });
            response.setResponse("Success");
        } catch (Exception e) {
            // exception
            logger.error(e.toString(), e);
            response.setResponse("Fail : " + e.toString());
        } finally {
            producer.flush();
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