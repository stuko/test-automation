package com.auto.test.jmeter.plugin.common.sampler;

import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.auto.test.jmeter.plugin.common.gui.TestAutomationGuiController;
import com.auto.test.jmeter.plugin.common.run.executor.TestPluginExecutor;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractTestPluginSampler extends AbstractSampler {

    static Logger logger = LoggerFactory.getLogger(TestPluginSampler.class);
    public TestPluginExecutor executor;

    @Override
    public SampleResult sample(Entry e) {
        if(e != null) logger.warn("Entry is {}", e.toString());
        prepare_sampler();
        return runSample(invoke_sample_result());
    }

    public abstract SampleResult runSample(SampleResult samplerResult);

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

    public SampleResult invoke_sample_result(){
        SampleResult sr = new SampleResult();
        sr.setSampleLabel(this.getExecutor().getTestData().getName());
        sr.sampleStart();
        sr.setDataType(SampleResult.TEXT);
        return record_execute_response(sr);
    }

    private SampleResult record_execute_response(SampleResult sr){
        long start = sr.currentTimeInMillis();
        TestPluginResponse response = null;
        try {
            logger.info("Sampler is Same ? {}" , this.hashCode());
            getExecutor().start();
            logger.info("before execute, Current Test Data Queue size is {}", getExecutor().getTestData().getTestDatas().size());
            response = getExecutor().execute();
            //#####################################################
            // 아래 처럼 테스트가 종료 되면, 테스트 데이터 생성 데몬을 종료시켜야 함.
            //#####################################################
            // getExecutor().stop();

            if(getExecutor().getTestData() == null || getExecutor().getTestData().getTestDatas() == null )
                logger.info("after execute, Current Test Data Queue(NULL) size is 0");
            else
                logger.info("after execute, Current Test Data Queue size is {}", getExecutor().getTestData().getTestDatas().size());

            if(response == null) logger.info("Execute result is null");
            else  logger.info("Execute result is Not null");
            try {
                if(response.getRequest() != null) {
                    getExecutor().writeRequestToJMeterContext(response.getRequest());
                }
            }catch(Exception ee) {logger.error(ee.toString());}
            sr.setSamplerData(response.getRequest());
            sr.setBytes(response.getSize());
            sr.setResponseData(response.getRequest().getBytes());
            sr.setSuccessful(true);
            sr.setResponseCodeOK();
            sr.setResponseHeaders(response.getRequest());
            sr.setResponseMessage(response.getExecuteTime()+"");
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

    public void prepare_sampler(){
        if(this.getExecutor() == null) {
            logger.info("##### TEST EXECUTOR IS NULL, so Refer Default Executor");
            this.setExecutor(TestAutomationGuiController.get_test_executor("DEFAULT", null));
        }else {
            logger.info("$$$$$ TEST EXECUTOR IS NOT NULL");
        }
        if(this.getExecutor().getTestData().getData() == null){
            logger.info("##### TEST DATA IS NULL, so Prepare Test Data");
            prepare_test_data();
        }else{
            logger.info("$$$$$ TEST DATA IS NOT NULL");
        }
    }

    public void prepare_test_data(){
        if(this.getExecutor().is_stop()) {
            logger.info("executor's mode is stop");
            if(this.getExecutor().getTestData().getData() == null){
                logger.info("executor's test data is null");
                TestAutomationGuiController.get_test_data_by_jmx(list -> {
                    String[][] test_data_factors = new String[list.size()][];
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> factor = list.get(i);
                        String[] row = new String[factor.size()];

                        row[0] = (String) factor.get("name");
                        row[1] = (String) factor.get("type");
                        row[2] = (String) factor.get("value");
                        row[3] = (String) factor.get("count");
                        row[4] = (String) factor.get("length");
                        row[5] = (String) factor.get("encode");
                        test_data_factors[i] = row;
                    }
                    getExecutor().getTestData().setData(test_data_factors);
                });
            }
            // this.getExecutor().start();
        }else logger.info("executor's mode is start");
    }
}
