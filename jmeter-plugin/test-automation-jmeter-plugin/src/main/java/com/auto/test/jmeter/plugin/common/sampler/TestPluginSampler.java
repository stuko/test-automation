package com.auto.test.jmeter.plugin.common.sampler;

import com.google.gson.Gson;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auto.test.jmeter.plugin.common.run.executor.TestPluginExecutor;

public class TestPluginSampler extends AbstractSampler {

    static Logger logger = LoggerFactory.getLogger(TestPluginSampler.class);
    public TestPluginExecutor executor;

    @Override
    public SampleResult sample(Entry e) {
        if(e != null) logger.warn("Entry is {}", e.toString());
        logger.warn("Sampler in Sampler is {}", this.toString());
        if(this.getExecutor() == null)logger.info("##### TEST EXECUTOR IS NULL");

        SampleResult sr = new SampleResult();
        sr.setSampleLabel(executor.getTestData().getName());
        sr.sampleStart();
        sr.setDataType(SampleResult.TEXT);
        long start = sr.currentTimeInMillis();
        TestPluginResponse response = null;
        try {
            logger.info("Sampler is Same ? {}" , this.hashCode());
            response = getExecutor().execute();
            if(response == null) logger.info("Execute result is null");
            else  logger.info("Execute result is Not null");
            sr.setSamplerData(response.getRequest());
            sr.setBytes(response.getSize());
            sr.setResponseData(response.getRequest().getBytes());
            sr.setSuccessful(true);
            sr.setResponseCodeOK();
            sr.setResponseHeaders(response.getRequest());
            sr.setResponseMessage(new Gson().toJson(response.getExecuteTime()));
        } catch (Exception ex) {
            logger.error(ex.toString(),ex);
            sr.setSuccessful(false);
        } finally {
            if(response.getResponse() != null) sr.setResponseData(response.getResponse().getBytes());
            else sr.setResponseData("No Response".getBytes());
            long latency = System.currentTimeMillis() - start;
            sr.sampleEnd();
            sr.setLatency(latency);
        }
        return sr;
    }

    public TestPluginExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(TestPluginExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Object clone(){
        TestPluginSampler sampler = (TestPluginSampler)super.clone();
        sampler.setExecutor(this.getExecutor());
        return sampler;
    }
}
