package com.kcb.fds.jmeter.plugin.common.factor.value;

import com.kcb.fds.jmeter.plugin.common.factor.value.fake.FakeData;
import com.kcb.fds.jmeter.plugin.common.factor.value.fake.FakeMetaData;
import com.kcb.fds.jmeter.plugin.common.util.SecurityUtil;

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
                    if(this.getFdsPluginMessageFactor().isEncode()){
                        final_result.add(SecurityUtil.encode(fakeData.value()));
                    }else final_result.add(fakeData.value());
                }
            }else {
                if(this.getFdsPluginMessageFactor().isSubstring()) s = s.substring(this.getFdsPluginMessageFactor().getSubstring_first_index(),this.getFdsPluginMessageFactor().getSubstring_second_index());
                if(this.getFdsPluginMessageFactor().isEncode())  s = SecurityUtil.encode(s);
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
            if(this.getFdsPluginMessageFactor().isEncode()){
                return SecurityUtil.encode(fakeData.value());
            }else return fakeData.value();
        }else {
            if(this.getFdsPluginMessageFactor().isSubstring()) s = s.substring(this.getFdsPluginMessageFactor().getSubstring_first_index(),this.getFdsPluginMessageFactor().getSubstring_second_index());
            if(this.getFdsPluginMessageFactor().isEncode())  s = SecurityUtil.encode(s);
            return s;
        }
    }
}
