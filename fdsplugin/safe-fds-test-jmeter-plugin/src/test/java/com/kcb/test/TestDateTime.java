package com.kcb.test;

import com.kcb.fds.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.kcb.fds.jmeter.plugin.common.factor.value.TestPluginMessageFactorDatetimeValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestDateTime {
    @Test
    void testDataTime(){
        boolean result = false;
        try{
            TestPluginMessageFactorDatetimeValue time = new TestPluginMessageFactorDatetimeValue("20210301000000","20210331000000",10, TestPluginMessageFactorDatetimeValue.Format.YYYYMMDDHHmmss);
            time.setFdsPluginMessageFactor(new TestPluginMessageFactorImplFactory("test","datetime","",10, false));
            List<String> r = time.getValues();
            for(String s : r){
                System.out.println(s);
            }
            result = true;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
            result = false;
        }

        TestPluginMessageFactorImplFactory factory = new TestPluginMessageFactorImplFactory("txDt", "datetime", "-6~0,day,yyyymmddHHmmss", 10, false);
        System.out.println("----------------");
        System.out.println(factory.getFactorRange().toString());
        System.out.println("----------------");
        factory = new TestPluginMessageFactorImplFactory("txDt", "datetime", "20211201000000~20211231000000,day,yyyymmddHHmmss", 10, false);
        System.out.println("----------------");
        System.out.println(factory.getFactorRange().toString());
        System.out.println("----------------");
        assertTrue(result);
    }
}
