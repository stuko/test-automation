package com.auto.test;

import org.junit.jupiter.api.Test;

public class TestReplace {
    @Test
    void testReplace(){
        String data = "\"hi\"";
        System.out.println(data);
        System.out.println(data.replaceAll("\"",""));
    }
}
