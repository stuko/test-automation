package com.auto.test.jmeter.plugin.common.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPluginTestDataImpl implements TestPluginTestData{

    static Logger logger = LoggerFactory.getLogger(TestPluginTestDataImpl.class);

    private String name;
    private String connectionInfo;
    // 테스트 데이터 메타 정보
    private String[][] data;
    // 테스트 데이터 실제 물리적 파일 큐 정보
    private FileJsonArrayList testDatas;

    public TestPluginTestDataImpl(String name){
        this.setName(name);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(String connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        if(data == null){
            logger.warn("############ Plugin Data is NULL ###############");
            return;
        }
        this.data = data;
    }

    @Override
    public String next() {
        return this.getTestDatas().next();
    }
    @Override
    public FileJsonArrayList getTestDatas() {
        return testDatas;
    }
    @Override
    public void setTestDatas(FileJsonArrayList testDatas) {
        this.testDatas = testDatas;
    }
}
