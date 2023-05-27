package com.auto.test.jmeter.plugin.common.data;

import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPluginTestDataQueueImpl extends TestPluginTestDataImpl{
    static Logger logger  = LoggerFactory.getLogger(TestPluginTestDataQueueImpl.class);

    public TestPluginTestDataQueueImpl(String name) {
        super(name);
    }
    @Override
    public String next() {
        String data = "{\"error\": -1}";
        try {
            // data = FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).next();
            logger.info("TestPluginTestDataQueue will fetch test data from queue");
            data = super.next();
            logger.info("TestPluginTestDataQueue fetched test data from queue");
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        return data;
    }

}
