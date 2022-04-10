package com.kcb.fds.jmeter.plugin.common.data;

import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayList;

public interface TestPluginTestData {
    public String getName();
    void setName(String name);
    String getConnectionInfo();
    void setConnectionInfo(String connectionInfo);
    String[][] getData();
    void setData(String[][] data);
    FileJsonArrayList getTestDatas();
    void setTestDatas(FileJsonArrayList testDatas);
    String next();
}
