package com.kcb.fds.jmeter.plugin.common.function;

@FunctionalInterface
public interface HttpCallBack {
    void response(String body);
}
