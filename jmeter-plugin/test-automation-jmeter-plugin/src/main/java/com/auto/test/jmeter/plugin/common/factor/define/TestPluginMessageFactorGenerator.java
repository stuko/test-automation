package com.auto.test.jmeter.plugin.common.factor.define;

import com.auto.test.jmeter.plugin.common.factor.range.TestPluginMessageFactorRangeCollection;

import java.util.*;
import com.auto.test.jmeter.plugin.common.factor.value.TestPluginMessageFactorValue;

public class TestPluginMessageFactorGenerator {

    Map<String,List<TestPluginMessageFactorRangeCollection>> ranges = new HashMap<>();

    public void add(String name, TestPluginMessageFactorRangeCollection... range ){
        if(this.ranges.get(name) == null)this.ranges.put(name, new ArrayList<>());
        for(TestPluginMessageFactorRangeCollection r : range){
            ranges.get(name).add(r);
        }
    }

    public List<String> getAllValue(String name){
        List<String> result = new ArrayList<>();
        for(TestPluginMessageFactorRangeCollection r : this.ranges.get(name)){
            for(TestPluginMessageFactorValue v : r.getRanges().values()){
                result.addAll(v.getValues());
            }
        }
        return result;
    }

    public List<String> getRandomValue(String name){
        List<String> result = new ArrayList<>();
        for(TestPluginMessageFactorRangeCollection r : this.ranges.get(name)){
            Iterator<TestPluginMessageFactorValue> it = r.getRanges().values().iterator();
            int max = r.getRanges().values().size();
            int x = 0;
            int rnd = (int)((Math.random() * (max - 0 + 1)) + 0);
            TestPluginMessageFactorValue v = null;
            while(x++ != rnd) v = it.next();
            result.addAll(v.getValues());
        }
        return result;
    }



}
