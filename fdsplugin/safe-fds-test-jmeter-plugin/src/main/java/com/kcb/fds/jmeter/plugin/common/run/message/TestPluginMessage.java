package com.kcb.fds.jmeter.plugin.common.run.message;

import com.kcb.fds.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;

import java.util.ArrayList;
import java.util.List;

public class TestPluginMessage {

    private List<TestPluginMessageFactorImplFactory> bodys = new ArrayList<>();
    public List<TestPluginMessageFactorImplFactory> getBodys() {
        return bodys;
    }

    public TestPluginMessageFactorImplFactory addBody(String name, String type, String value, boolean encode){
        TestPluginMessageFactorImplFactory f = new TestPluginMessageFactorImplFactory(name,type,value,10, encode);
        this.getBodys().add(f);
        return f;
    }
    public TestPluginMessageFactorImplFactory addBody(String name, String type, String value, int cnt, boolean encode){
        TestPluginMessageFactorImplFactory f = new TestPluginMessageFactorImplFactory(name,type,value,cnt, encode);
        this.getBodys().add(f);
        return f;
    }

    public void clear(){
        bodys = new ArrayList<>();
    }
}
