package com.auto.test.jmeter.plugin.common.function;

@FunctionalInterface
public interface HttpCallBack {
    void response(String body);
}
