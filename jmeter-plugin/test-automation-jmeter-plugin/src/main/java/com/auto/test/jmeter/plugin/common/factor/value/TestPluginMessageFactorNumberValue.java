package com.auto.test.jmeter.plugin.common.factor.value;

import com.auto.test.jmeter.plugin.common.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

public class TestPluginMessageFactorNumberValue extends AbstractTestPluginMessageFactorValueImpl {

    private long from;
    private long to;
    private int count;

    public TestPluginMessageFactorNumberValue(long from, long to, int count){
        this.setValueType(Type.Number);
        this.setFrom(from);
        this.setTo(to);
        this.setCount(count);
    }

    @Override
    public List<String> getValues() {
        List<String> result = new ArrayList<>();
        for(int i = 0; i < this.count; i++) {
            int r = (int)((Math.random() * (this.getFrom() - this.getTo() + 1)) + this.getTo());
            if(this.getTaPluginMessageFactor().isEncode()){
                result.add(SecurityUtil.encode(r+""));
            }else result.add(r+"");
        }
        return result;
    }

    public String toString() {
        int r = (int)((Math.random() * (this.getFrom() - this.getTo() + 1)) + this.getTo());
        if(this.getTaPluginMessageFactor().isEncode()){
            return SecurityUtil.encode(r+"");
        }else return r+"";
    }

    public long getFrom() {
        return from;
    }
    public void setFrom(long from) {
        this.from = from;
    }
    public long getTo() {
        return to;
    }
    public void setTo(long to) {
        this.to = to;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
