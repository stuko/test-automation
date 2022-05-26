package com.auto.test.jmeter.plugin.common.data;

import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

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
            data = super.next();
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        return data;
    }

}
