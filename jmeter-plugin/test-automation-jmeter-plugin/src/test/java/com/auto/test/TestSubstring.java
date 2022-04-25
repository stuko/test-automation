package com.auto.test;


import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import org.junit.jupiter.api.Test;

public class TestSubstring {
    @Test
    void testSubstring(){
        String src = "@SUBSTR(0,day,yyyyMMdd) FROM(0) LENGTH(4)"; // "@SUBSTR(데이터) FROM(start) LENGTH(length)";
        if(src.startsWith(TestPluginConstants.ta_substring_data)){
            if(src.indexOf(" ") > 0) {
                String[] substrs = src.split(" ");
                if(substrs.length == 3) {
                    String fun = substrs[0];
                    String from = substrs[1];
                    String length = substrs[2];
                    String data = testGetSubstringData(fun);
                    from = testGetSubstringData(from);
                    length = testGetSubstringData(length);
                    System.out.println("data = "+ data);
                    System.out.println("from = "+ from);
                    System.out.println("length = "+ length);

                }else if(substrs.length == 2) {
                    String fun = substrs[0];
                    String from = substrs[1];
                    String data = testGetSubstringData(fun);
                    from = testGetSubstringData(from);
                }else{
                    System.out.println("ERR");
                }
            }

        }
    }

    @Test
    String testGetSubstringData(String data){
        data = data.substring(data.indexOf("(")+1,data.indexOf(")"));
        return data;
    }

}
