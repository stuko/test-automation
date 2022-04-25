package com.auto.test.jmeter.plugin.common.sampler;

public class TestPluginResponseImpl implements TestPluginResponse {

    private long size = 0;
    private String response = "";
    private String request = "";

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getRequest() {
        return request;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public long getExecuteTime() {
        return 0;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
