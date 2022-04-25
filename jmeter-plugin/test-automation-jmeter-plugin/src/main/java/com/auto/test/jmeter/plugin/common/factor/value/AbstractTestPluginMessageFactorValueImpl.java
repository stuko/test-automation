package com.auto.test.jmeter.plugin.common.factor.value;


import java.util.List;
import com.auto.test.jmeter.plugin.common.factor.define.TestPluginMessageSubstringFactor;

public abstract class AbstractTestPluginMessageFactorValueImpl implements TestPluginMessageFactorValue {
    private TestPluginMessageFactorValue.Type valueType;
    TestPluginMessageSubstringFactor taPluginMessageFactor;

    @Override
    public Type getValueType() {
        return valueType;
    }

    @Override
    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    @Override
    public TestPluginMessageSubstringFactor getTaPluginMessageFactor() {
        return taPluginMessageFactor;
    }

    @Override
    public void setTaPluginMessageFactor(TestPluginMessageSubstringFactor taPluginMessageFactor) {
        this.taPluginMessageFactor = taPluginMessageFactor;
    }

    @Override
    public abstract List<String> getValues();

}
