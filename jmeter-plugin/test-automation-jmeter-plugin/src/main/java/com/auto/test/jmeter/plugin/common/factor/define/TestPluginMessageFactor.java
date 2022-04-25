package com.auto.test.jmeter.plugin.common.factor.define;

public interface TestPluginMessageFactor {
    String getName();
    void setName(String name);
    String getValue();
    void setValue(String value);
    void setType(String type);
    int getCount();
    void setCount(int count);
    String getLength();
    void setLength(String length);
    boolean isEncode();
    void setEncode(boolean encode);
    String getLengthString(String source, int len);
}
