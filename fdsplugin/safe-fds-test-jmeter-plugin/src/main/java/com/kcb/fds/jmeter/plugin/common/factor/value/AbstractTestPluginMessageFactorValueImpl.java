package com.kcb.fds.jmeter.plugin.common.factor.value;


import java.util.List;
import com.kcb.fds.jmeter.plugin.common.factor.define.TestPluginMessageSubstringFactor;

public abstract class AbstractTestPluginMessageFactorValueImpl implements TestPluginMessageFactorValue {
    private TestPluginMessageFactorValue.Type valueType;
    TestPluginMessageSubstringFactor fdsPluginMessageFactor;

    @Override
    public Type getValueType() {
        return valueType;
    }

    @Override
    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    @Override
    public TestPluginMessageSubstringFactor getFdsPluginMessageFactor() {
        return fdsPluginMessageFactor;
    }

    @Override
    public void setFdsPluginMessageFactor(TestPluginMessageSubstringFactor fdsPluginMessageFactor) {
        this.fdsPluginMessageFactor = fdsPluginMessageFactor;
    }

    @Override
    public abstract List<String> getValues();

}
