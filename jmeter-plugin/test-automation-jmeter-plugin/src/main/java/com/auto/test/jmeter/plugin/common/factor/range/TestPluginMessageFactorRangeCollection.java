package com.auto.test.jmeter.plugin.common.factor.range;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.auto.test.jmeter.plugin.common.factor.value.TestPluginMessageFactorValue;

public class TestPluginMessageFactorRangeCollection {

    static Logger logger = LoggerFactory.getLogger(TestPluginMessageFactorRangeCollection.class);

    private boolean cache = false;
    private Map<String, TestPluginMessageFactorValue> ranges = new HashMap<>();
    public Map<String, TestPluginMessageFactorValue> getRanges() {
        return ranges;
    }
    public void setRanges(Map<String, TestPluginMessageFactorValue> ranges) {
        this.ranges = ranges;
    }
    List<String> result;

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public List<String> getValues(){
        if(this.isCache() && result != null){
            return result;
        }else {
            result = new ArrayList<>();
            for (String rangeName : this.getRanges().keySet()) {
                TestPluginMessageFactorValue rangeValue = this.getRanges().get(rangeName);
                result.addAll(rangeValue.getValues());
            }
        }
        return result;
    }

    @Override
    public String toString(){
        logger.info("size = {}", this.getRanges().size());
        logger.info("data = {}", this.getRanges());
        return this.getValues().get(0);
    }
}
