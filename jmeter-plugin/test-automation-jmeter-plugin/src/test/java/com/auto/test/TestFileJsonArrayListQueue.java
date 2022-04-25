package com.auto.test;

import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestFileJsonArrayListQueue {
    static Logger logger = LoggerFactory.getLogger(TestFileJsonArrayListQueue.class);
    @Test
    void testFileJsonArrayListQueue(){
        boolean result = false;
        try {
            FileJsonArrayListQueue fjalp = FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path);
            new Thread(()->{
                int i = 0;
                while(true){
                    fjalp.write("{\"name\" : \"stuko"+ ++i +"\"}");
                    fjalp.write("{\"name\" : \"stuko"+ ++i +"\"}");
                    fjalp.write("{\"name\" : \"stuko"+ ++i +"\"}");
                    fjalp.write("{\"name\" : \"stuko"+ ++i +"\"}");
                    fjalp.write("{\"name\" : \"stuko"+ ++i +"\"}");
                    //fjalp.writeLast();
                    try{System.out.println("wait.... 200msec");Thread.sleep(200);}catch(Exception e){logger.error(e.toString());}
                    if(i >= 10000000) break;
                }
            }).start();

            new Thread(()->{
                int i = 0;
                while(true){
                    ++i;
                    System.out.println(fjalp.getTestDataFilePath().getAbsolutePath() + "," + fjalp.next());
                    try{System.out.println("wait.... 3000msec");Thread.sleep(3000);}catch(Exception e){logger.error(e.toString());}
                    if(i >= 10000000) break;
                }
            }).start();
            result = true;
        }catch(Exception e){
            System.out.println(e.toString());
            result = false;
        }
        assertTrue(result);
    }
}
