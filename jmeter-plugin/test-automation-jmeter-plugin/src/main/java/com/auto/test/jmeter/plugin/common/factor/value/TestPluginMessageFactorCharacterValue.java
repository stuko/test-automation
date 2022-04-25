package com.auto.test.jmeter.plugin.common.factor.value;

import com.auto.test.jmeter.plugin.common.factor.value.fake.FakeData;
import com.auto.test.jmeter.plugin.common.factor.value.fake.FakeMetaData;
import com.auto.test.jmeter.plugin.common.util.SecurityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPluginMessageFactorCharacterValue extends AbstractTestPluginMessageFactorValueImpl {

    FakeMetaData fakeMetaData = new FakeMetaData();
    private List<String> result = new ArrayList<>();

    public TestPluginMessageFactorCharacterValue(String... data) {
        this.setValueType(Type.Character);
        setResult(Arrays.asList(data));
    }

    @Override
    public List<String> getValues() {
        return getResult();
    }

    public List<String> getResult() {
        List<String> final_result  = new ArrayList<>();
        result.forEach(s->{
            if(fakeMetaData.isFakeData(s)){
                fakeMetaData.importData(s);
                for (FakeData fakeData : fakeMetaData.getFakeDataList()) {
                    if(this.getTaPluginMessageFactor().isEncode()){
                        final_result.add(SecurityUtil.encode(fakeData.value()));
                    }else final_result.add(fakeData.value());
                }
            }else {
                if(this.getTaPluginMessageFactor().isSubstring()) s = s.substring(this.getTaPluginMessageFactor().getSubstring_first_index(),this.getTaPluginMessageFactor().getSubstring_second_index());
                if(this.getTaPluginMessageFactor().isEncode())  s = SecurityUtil.encode(s);
                final_result.add(s);
            }
        });
        return final_result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public String toString(){
        String s = this.result.get(0);
        if(fakeMetaData.isFakeData(s)){
            fakeMetaData.importData(s);
            FakeData fakeData = fakeMetaData.getFakeDataList().get(0);
            if(this.getTaPluginMessageFactor().isEncode()){
                return SecurityUtil.encode(fakeData.value());
            }else return fakeData.value();
        }else {
            if(this.getTaPluginMessageFactor().isSubstring()) s = s.substring(this.getTaPluginMessageFactor().getSubstring_first_index(),this.getTaPluginMessageFactor().getSubstring_second_index());
            if(this.getTaPluginMessageFactor().isEncode())  s = SecurityUtil.encode(s);
            return s;
        }
    }
}
