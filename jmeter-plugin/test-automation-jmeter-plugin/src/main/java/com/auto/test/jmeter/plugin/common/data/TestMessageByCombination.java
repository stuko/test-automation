package com.auto.test.jmeter.plugin.common.data;

import com.google.gson.Gson;
import com.auto.test.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.auto.test.jmeter.plugin.common.gui.PluginProgressBar;
import com.auto.test.jmeter.plugin.common.run.message.TestMessage;
import com.auto.test.jmeter.plugin.common.function.CombinationCall;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.auto.test.jmeter.plugin.common.util.TestPluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;

public class TestMessageByCombination extends TestMessage {
    Logger logger = LoggerFactory.getLogger(TestMessageByCombination.class);

    @Override
    public FileJsonArrayList getFileMessage(TestPluginCallBack callBack) {

        logger.info("-------------------------------");
        logger.info("Start to generate test data");
        logger.info("-------------------------------");
        Gson gson = new Gson();
        logger.info("step 1. create list array for index array from source data");

        List<List<String>> factors_source = new ArrayList<>();
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            try {
                factors_source.add(factor.getFactorRange().getValues());
            }catch(Exception ex){
                logger.error("create index array Error["+factor.getName()+"] : " + ex.toString(), ex);
            }
        }

        logger.info("step 2. create combination's index array from list array");

        List<int[]> combination_list = getCombinationArray(this.message.getBodys());
        logger.info("this.message.getBodys().length = " + this.message.getBodys().size());
        logger.info("combination_list.length = " + combination_list.size());
        AtomicLong al = new AtomicLong();

        logger.info("step 3. create combination of  index array");

        this.combination(combination_list,null,0, 0, (l,a, col, count)->{
            Map<String,String> m = new HashMap<>();
            Map<String,String> ref = new HashMap<>();
            logger.info("combination_list.loop = " + l.size());
            try {
                for (int i = 0; i < a.length; i++) {
                    logger.info("name = {} , value = {}", this.message.getBodys().get(i).getName(), factors_source.get(i).get(l.get(i)[a[i]]));
                    if(TestPluginConstants.ta_random_key_data.equals(factors_source.get(i).get(l.get(i)[a[i]]))) {
                        m.put(this.message.getBodys().get(i).getName(), TestPluginMessageFactorImplFactory.getRandomCharacter(TestPluginConstants.ta_random_char_length).toString()+"_"+System.nanoTime());
                        // System.out.println("RAND : " + factors_source.get(i).get(l.get(i)[a[i]]));
                    }else if(factors_source.get(i).get(l.get(i)[a[i]]).startsWith(TestPluginConstants.ta_ref_data)) {
                        String ref_key = TestPluginUtil.getSubstringData(factors_source.get(i).get(l.get(i)[a[i]]));
                        ref.put(this.message.getBodys().get(i).getName(),ref_key);
                        // System.out.println("RAND : " + factors_source.get(i).get(l.get(i)[a[i]]));
                    }else{
                        m.put(this.message.getBodys().get(i).getName(), factors_source.get(i).get(l.get(i)[a[i]]));
                        // System.out.println("NOT RAND : " + factors_source.get(i).get(l.get(i)[a[i]]));
                    }
                    //m.put(this.message.getBodys().get(i).getName(), factors_source.get(i).get(l.get(i)[a[i]]));
                }
                for(String k : ref.keySet()){
                    ref.put(k,m.get(ref.get(k)));
                }
            }catch(Exception e){
                logger.error(e.toString(),e);
            }
            m.putAll(ref);
            logger.info("before al = " + al.intValue());
            callBack.call(gson.toJson(m),al.incrementAndGet());

            logger.info("after al = " + al.intValue());
        });
        logger.info("step 4. combination job is completed and process last queue ");
        FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).writeLast();

        logger.info("step 5. end. ");
        return FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path);
    }

    public List<int[]> getCombinationArray(List<TestPluginMessageFactorImplFactory> lists){
        List<int[]> combination_source = new ArrayList<>();
        lists.forEach(f->{
            int[] combination_int = new int[f.getFactorRange().getValues().size()];
            for(int i = 0; i < combination_int.length; i++){
                combination_int[i] = i;
            }
            combination_source.add(combination_int);
        });
        return combination_source;
    }

    public int combination(List<int[]> combination_source,int[] combination_result, int x, int count, CombinationCall combinationCall) {
        if(combination_result == null) combination_result = new int[combination_source.size()];
        logger.debug("-----------combination-------------");
        logger.debug("combination start " + x + "," + count + "," + combination_source.size());
        logger.debug("-----------combination-------------");
        if(x < combination_source.size()-1) {
        	logger.debug("combination : x < combination_source.size()-1");
        	logger.debug("-----------combination loop -------------");
            logger.debug("combination_source.get(x).length : {}", combination_source.get(x).length);
            logger.debug("this.isStop() : {}", this.isStop());
            logger.debug("-----------combination loop -------------");
            for (int y = 0; y < combination_source.get(x).length && !this.isStop(); y++) {
            	combination_result[x] = combination_source.get(x)[y];
            	logger.debug("combination loop " + combination_result[x] );
                if (combination_source.size() == 1) {
                    if (x == 0) {
                        count++;
                        if(this.isStop()) break;
                        combinationCall.call(combination_source, combination_result, x, count);
                    }
                } else {
                    if (x < combination_source.size() - 1) {
                        for(int i = 0; i < combination_result.length; i++){
                            if(i >= x+1) combination_result[i] = -1;
                        }
                        if(this.isStop()) break;
                        count = combination(combination_source, combination_result, x + 1, count, combinationCall);
                    }
                }
                logger.debug("current x,y = "+ x + "," + y);
            }
        }else if(x == combination_source.size()-1){
        	logger.debug("combination : x == combination_source.size()-1");
            for (int y = 0; y < combination_source.get(x).length; y++) {
                count++;
                combination_result[x] = combination_source.get(x)[y];
                logger.debug("combination loop " + combination_result[x] );
                if(this.isStop()) break;
                combinationCall.call(combination_source, combination_result, x, count);
            }
        }else {
        	logger.debug("combination loop");
        }
        return count;
    }



}
