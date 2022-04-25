package com.auto.test;

import com.auto.test.jmeter.plugin.common.util.SecurityUtil;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class TestSecurityUtil {

    @Test
    void testSecurityUtil(){
        boolean result = false;
        try {
            System.out.println(SecurityUtil.encode("TEST"));
            /*
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            String script = "var test = function(){console.log('test')};";
            engine.eval(script);
            Invocable invocable = (Invocable) engine;
            Object invoke_result = invocable.invokeFunction("test");
            System.out.println(invoke_result);
            */
            result = true;
        }catch(Exception e){
            System.out.println(e.toString());
            result = false;
        }
        assertTrue(result);
    }
}
