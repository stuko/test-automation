package com.auto.test.jmeter.plugin.common.factor.define;

public interface TestPluginMessageSubstringFactor extends TestPluginMessageFactor {
    boolean isSubstring();
    void setSubstring(boolean substring);
    int getSubstring_first_index();
    void setSubstring_first_index(int substring_first_index);
    int getSubstring_second_index();
    void setSubstring_second_index(int substring_second_index);
}
