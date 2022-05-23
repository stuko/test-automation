package com.auto.test.jmeter.plugin.common.sampler;

import org.apache.jmeter.samplers.SampleResult;

public class TestPluginSampler extends AbstractTestPluginSampler {
    @Override
    public SampleResult runSample(SampleResult sample){
        return sample;
    }
}
