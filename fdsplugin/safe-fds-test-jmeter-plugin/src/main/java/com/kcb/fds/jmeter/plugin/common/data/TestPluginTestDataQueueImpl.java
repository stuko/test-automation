package com.kcb.fds.jmeter.plugin.common.data;

import com.kcb.fds.jmeter.plugin.common.util.TestPluginConstants;
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
            data = FileJsonArrayListQueue.getInstance(TestPluginConstants.fds_data_path).next();
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
        return data;
    }

}
