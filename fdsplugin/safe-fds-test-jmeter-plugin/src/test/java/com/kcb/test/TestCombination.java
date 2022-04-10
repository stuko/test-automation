package com.kcb.test;

import com.google.gson.Gson;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginConstants;
import com.kcb.fds.jmeter.plugin.common.data.TestMessageByCombination;
import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListQueue;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class TestCombination {

    static Logger logger = LoggerFactory.getLogger(TestCombination.class);
    long combination_max = 200;
    Gson gson = new Gson();

    @Test
    void testCombination(){
        boolean result = false;
        try {
            // 항목의 범위는 평균 4가지 값
            // 전체 항목의 갯수는 약 200여개
            List<int[]> lists = new ArrayList<>();
            //lists.add(new int[]{1});
            //lists.add(new int[]{1});
            lists.add(new int[]{11, 4, 5, 6});
            lists.add(new int[]{21, 3, 4, 7});
            lists.add(new int[]{0, 2, 4, 8});
            // for(int i = 0; i < combination_max; i++) lists.add(new int[]{11, 12, 14, 4});
            // expected combination count
            double expected_combination_count = Math.pow(4, lists.size());
            System.out.println("expected_combination_count : " + expected_combination_count);
            if(expected_combination_count > 10000000000L) {
                System.out.println("Too many case...................");
                return;
            }
            TestMessageByCombination q = new TestMessageByCombination();
            List<int[]> combination_list = new ArrayList<>();
            int pos = 0;
            for (int[] x : lists) {
                combination_list.add(new int[x.length]);
                for (int i = 0; i < x.length; i++) {
                    combination_list.get(pos)[i] = i;
                }
                pos++;
            }
            int cnt = q.combination(combination_list, null, 0, 0, (source, target, col, count) -> {
                System.out.println("Index is " + Arrays.toString(target) + " col, count = " + col + "," + count);
            });

            System.out.println("total count is  " + cnt);
            //combination_list.forEach(l -> {
            //    System.out.println(Arrays.toString(l));
            //});
            assertTrue((int)expected_combination_count == cnt);
            result = true;
        }catch(Exception e){
            System.out.println(e.toString());
            result = false;
        }
        assertTrue(result);
    }

    @Test
    void testCombinationWithQueue(){
        boolean result = false;
        try {
            // 항목의 범위는 평균 4가지 값
            // 전체 항목의 갯수는 약 200여개
            List<int[]> lists = new ArrayList<>();
            //lists.add(new int[]{1});
            //lists.add(new int[]{1});
            lists.add(new int[]{11, 4, 5, 6});
            lists.add(new int[]{21, 3, 4, 7});
            lists.add(new int[]{0, 2, 4, 8});
            for(int i = 0; i < combination_max; i++) lists.add(new int[]{11, 12, 14, 4});
            // expected combination count
            double expected_combination_count = Math.pow(4, lists.size());
            System.out.println("expected_combination_count : " + expected_combination_count);

            TestMessageByCombination q = new TestMessageByCombination();
            List<int[]> combination_list = new ArrayList<>();
            int pos = 0;
            for (int[] x : lists) {
                combination_list.add(new int[x.length]);
                for (int i = 0; i < x.length; i++) {
                    combination_list.get(pos)[i] = i;
                }
                pos++;
            }
            AtomicLong al = new AtomicLong();
            FileJsonArrayListQueue file_json_arraylist_queue = FileJsonArrayListQueue.getInstance(TestPluginConstants.fds_data_path);
            int cnt = q.combination(combination_list, null, 0, 0, (source, target, col, count) -> {
                System.out.println("Index is " + Arrays.toString(target) + " col, count = " + col + "," + count);
                Map<String,String> m = new HashMap<>();
                try {
                    for (int i = 0; i < target.length; i++) {
                        m.put("name-" + i , source.get(i)[target[i]]+"");
                    }
                }catch(Exception e){
                    logger.error(e.toString(),e);
                }
                al.incrementAndGet();
                file_json_arraylist_queue.write(gson.toJson(m));
            });

            System.out.println("total count is  " + cnt);
            //combination_list.forEach(l -> {
            //    System.out.println(Arrays.toString(l));
            //});
            assertTrue((int)expected_combination_count == cnt);
            result = true;
        }catch(Exception e){
            System.out.println(e.toString());
            result = false;
        }
        assertTrue(result);
    }
}
