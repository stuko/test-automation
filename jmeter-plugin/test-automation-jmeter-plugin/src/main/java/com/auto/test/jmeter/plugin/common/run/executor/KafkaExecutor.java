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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    String name = "KafkaExecutor";
    @Override
    public void init(TestPluginTestData data , TestPluginCallBack callBack) {
        try {
            super.init(data,callBack);
            if(this.getConfigMap() != null) {
                Properties properties = new Properties();
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getConfigMap().get("server") + "");
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                producer = new KafkaProducer<>(properties);
                topicName = this.getConfigMap().get("topic") + "";
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    @Override
    public TestPluginResponse execute() {
        // String data = FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).next();
        String data = this.getTestData().next();
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
                // logger.error(e.toString(), e);
                response.setResponse("Fail : " + e.toString());
                response.setError(true);
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
            // logger.error(e.toString(), e);
            response.setResponse("Fail : " + e.toString());
            response.setError(true);
        } finally {
            producer.flush();
        }
        return response;
    }
}