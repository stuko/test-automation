package com.auto.test.jmeter.plugin.common.factor.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.common.factor.value.fake.FakeData;
import com.auto.test.jmeter.plugin.common.factor.value.fake.FakeMetaData;
import com.auto.test.jmeter.plugin.common.util.SecurityUtil;


public class TestPluginMessageFactorCharacterValue extends AbstractTestPluginMessageFactorValueImpl {

	static Logger logger = LoggerFactory.getLogger(TestPluginMessageFactorCharacterValue.class);
	
    FakeMetaData fakeMetaData = new FakeMetaData();
    private List<String> result = new ArrayList<>();
    private int count;

    public TestPluginMessageFactorCharacterValue(int count, String... data) {
        this.setValueType(Type.Character);
        setResult(Arrays.asList(data));
        this.setCount(count);
    }
    
    public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
    public List<String> getValues() {
        return getResult();
    }

    public List<String> getResult() {
        List<String> final_result  = new ArrayList<>();
        result.forEach(s->{
        	logger.debug("getResult s : {}", s);
            if(fakeMetaData.isFakeData(s)){
                fakeMetaData.importData(s);
                for (FakeData fakeData : fakeMetaData.getFakeDataList()) {
                	if(this.getCount() > 0) {
                		for(int i = 1; i <= this.getCount(); i++) {
                			if(this.getTaPluginMessageFactor().isEncode()){
    	                        final_result.add(SecurityUtil.encode(fakeData.value(i)));
    	                    }else final_result.add(fakeData.value(i));
                		}
                	}else {
	                    if(this.getTaPluginMessageFactor().isEncode()){
	                        final_result.add(SecurityUtil.encode(fakeData.value()));
	                    }else final_result.add(fakeData.value());
                	}
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
