package com.kcb.test;

import com.google.gson.Gson;
import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListPlus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestFileJsonArrayList {
    @Test
    void testFileJsonArrayList(){
        boolean result = false;
        try{
            FileJsonArrayListPlus listPlus = new FileJsonArrayListPlus("./data/","source.txt");
            Map<String,String> map = new HashMap<>();
            map.put("name","john");
            map.put("age","60");
            Gson gson = new Gson();
            listPlus.write(gson.toJson(map));
            Map<String,String> map2 = new HashMap<>();
            map2.put("name","smith");
            map2.put("age","22");
            listPlus.add(map2);
            Map<String,String> map3 = new HashMap<>();
            map3.put("name","jane");
            map3.put("age","19");
            listPlus.add(map3);
            Map<String,String> map4 = new HashMap<>();
            map4.put("name","홍길동");
            map4.put("age","55");
            listPlus.write(gson.toJson(map4));
            System.out.println(listPlus.get(3));
            FileJsonArrayListPlus listSub = new FileJsonArrayListPlus("./data/","target.txt");
            listPlus.forEach(m->{
                listSub.add(gson.fromJson(m,Map.class));
            });

            listPlus.readAll();
            listSub.readAll();

            System.out.println("Start Get process");
            for(int i = 1; i <= listPlus.size(); i++){
                listPlus.get(i);
            }
            for(int i = 1; i <= listSub.size(); i++){
                listSub.get(i);
            }
            System.out.println(listPlus.getTestDataFilePath().getAbsolutePath());
            System.out.println(listSub.getTestDataFilePath().getAbsolutePath());
            result = true;
        }catch(Exception e){
            System.out.println(e.toString());
            result = false;
        }
        assertTrue(result);
    }
}
