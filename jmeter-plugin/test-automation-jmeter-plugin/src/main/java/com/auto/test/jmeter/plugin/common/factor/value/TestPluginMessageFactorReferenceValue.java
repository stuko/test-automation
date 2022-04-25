package com.auto.test.jmeter.plugin.common.factor.value;

import com.auto.test.jmeter.plugin.common.factor.value.TestPluginMessageFactorKeyValue;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;

import java.util.Arrays;
import java.util.List;

public class TestPluginMessageFactorReferenceValue extends TestPluginMessageFactorKeyValue {

    String data;

    public TestPluginMessageFactorReferenceValue(String data) {
        super(0);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public List<String> getValues() {
        List<String> list = Arrays.asList(TestPluginConstants.ta_ref_data+"(" + this.getData() + ")");
        return list;
    }

    public String toString() {
        return TestPluginConstants.ta_ref_data+"(" + this.getData() + ")";
    }
}
