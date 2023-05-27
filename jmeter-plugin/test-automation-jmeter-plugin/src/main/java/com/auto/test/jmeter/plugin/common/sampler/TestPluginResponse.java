package com.auto.test.jmeter.plugin.common.sampler;

public interface TestPluginResponse {
    long getSize();
    String getRequest();
    String getResponse();
    long getExecuteTime();

    void setError(boolean error);
    boolean isError();
}
