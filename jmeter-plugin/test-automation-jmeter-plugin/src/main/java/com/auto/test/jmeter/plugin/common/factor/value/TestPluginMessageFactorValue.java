package com.auto.test.jmeter.plugin.common.factor.value;


import java.util.List;
import com.auto.test.jmeter.plugin.common.factor.define.TestPluginMessageSubstringFactor;

public interface TestPluginMessageFactorValue {
    public static enum Type{Character, Number, Datetime, Reference, File}
    List<String> getValues();
    void setValueType(Type t);
    Type getValueType();

    TestPluginMessageSubstringFactor getTaPluginMessageFactor();
    void setTaPluginMessageFactor(TestPluginMessageSubstringFactor messageFactor);
}
