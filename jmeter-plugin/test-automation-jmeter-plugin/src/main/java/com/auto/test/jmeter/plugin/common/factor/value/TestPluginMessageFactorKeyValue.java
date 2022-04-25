package com.auto.test.jmeter.plugin.common.factor.value;

import com.auto.test.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;

import java.util.Arrays;
import java.util.List;

public class TestPluginMessageFactorKeyValue extends TestPluginMessageFactorCharacterValue {
    int size;
    String prefix="";
    String postfix="";

    public TestPluginMessageFactorKeyValue(int size, String prefix, String postfix){
        this.size = size;
        this.prefix = prefix;
        this.postfix = postfix;
    }

    public TestPluginMessageFactorKeyValue(int size){
        this.size = size;
    }

    @Override
    public List<String> getValues() {
        // List<String> list = Arrays.asList(prefix+FdsPluginMessageFactor.getRandomCharacter(this.size).toString()+postfix);
        List<String> list = Arrays.asList(TestPluginConstants.ta_random_key_data);
        return list;
    }

    public String toString() {
        return prefix+ TestPluginMessageFactorImplFactory.getRandomCharacter(this.size).toString()+postfix;
    }

}
