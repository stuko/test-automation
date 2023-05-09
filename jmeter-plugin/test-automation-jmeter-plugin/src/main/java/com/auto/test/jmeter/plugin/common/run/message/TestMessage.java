package com.auto.test.jmeter.plugin.common.run.message;

import com.google.gson.Gson;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayList;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListPlus;
import com.auto.test.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.auto.test.jmeter.plugin.common.gui.PluginProgressBar;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.auto.test.jmeter.plugin.common.util.TestPluginUtil;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;

public class TestMessage extends TestPluginMessageBuilder {

    Logger logger = LoggerFactory.getLogger(TestMessage.class);
    int progress = 0;
    long total = 1;
    boolean showProgress = true;
    final PluginProgressBar probressBar = new PluginProgressBar(100,1 , 1000, ()->{
        return getProgress();
    });
    AtomicBoolean stop = new AtomicBoolean(true);

    public boolean isStop() {
        return stop.get();
    }

    public void setStop(boolean stop) {
        this.stop.set(stop);
    }

    public int getProgress(){
        return progress;
    }

    
    public String[] getStringMessage(TestPluginCallBack callBack) {
        List<Map<String,String>> result = new ArrayList<>();
        logger.info("start getStringMessage......");
        int varIdx = 1;
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            logger.info("loop bodys {}" , varIdx);
            List<String> rangeValueList = factor.getFactorRange().getValues();
            List<Map<String,String>>  tmp = new ArrayList<>();

            if(result.size() == 0){
                logger.info("Result size is Zero , so add {}" , rangeValueList.size());
                for(int x = 0; x < rangeValueList.size(); x++) {
                    tmp.add(new HashMap<>());
                }
            } else{
                logger.info("Result size is not Zero , so add {}" , rangeValueList.size()*result.size());
                for(int x = 0; x < rangeValueList.size(); x++) {
                    result.forEach(m ->{
                        tmp.add(m);
                    });
                }
            }

            int start = 0;

            Map<String,String> ref = new HashMap<>();
            for(; start < tmp.size(); ) {
                for (int rIdx = 0; rIdx < rangeValueList.size() ; rIdx++) {
                	logger.debug("rangeValueList's [{}] is {}",rIdx, rangeValueList.get(rIdx));
                    // tmp.get(start).put(factor.getName(), rangeValueList.get(rIdx));
                    if(TestPluginConstants.ta_random_key_data.equals(rangeValueList.get(rIdx))) {
                    	tmp.get(start).put(factor.getName(), TestPluginMessageFactorImplFactory.getRandomCharacter(Integer.parseInt(factor.getLength())).toString());
                    }else if(rangeValueList.get(rIdx).startsWith(TestPluginConstants.ta_ref_data)) {
                    	String ref_key = TestPluginUtil.getSubstringData(rangeValueList.get(rIdx));
                    	ref.put(factor.getName(),ref_key);
                    }else{
                    	tmp.get(start).put(factor.getName(), rangeValueList.get(rIdx));
                    }
//                    
//                    if(TestPluginConstants.ta_random_key_data.equals(factors_source.get(i).get(l.get(i)[a[i]]))) {
//                        m.put(this.message.getBodys().get(i).getName(), TestPluginMessageFactorImplFactory.getRandomCharacter(TestPluginConstants.ta_random_char_length).toString()+"_"+System.nanoTime());
//                        // System.out.println("RAND : " + factors_source.get(i).get(l.get(i)[a[i]]));
//                    }else if(factors_source.get(i).get(l.get(i)[a[i]]).startsWith(TestPluginConstants.ta_ref_data)) {
//                        String ref_key = TestPluginUtil.getSubstringData(factors_source.get(i).get(l.get(i)[a[i]]));
//                        ref.put(this.message.getBodys().get(i).getName(),ref_key);
//                        // System.out.println("RAND : " + factors_source.get(i).get(l.get(i)[a[i]]));
//                    }else{
//                        m.put(this.message.getBodys().get(i).getName(), factors_source.get(i).get(l.get(i)[a[i]]));
//                        // System.out.println("NOT RAND : " + factors_source.get(i).get(l.get(i)[a[i]]));
//                    }
                    logger.info("current record : " + tmp.get(start));
                    start++;
                }
            }
            result.addAll(tmp);
            progress = result.size();
            callBack.call(progress+"" , progress);
            varIdx++;
        }
        logger.info("Current message count is {}",result.size());
        for(int i = 0; i < result.size() && i < 10 ; i++){
            logger.info("Forward Sample["+i+"] message is {}",result.get(i));
        }
        for(int i = result.size()-1 ; i >= 0 && i > (result.size()-10) ; i--){
            logger.info("Backward Sample["+i+"] message is {}",result.get(i));
        }
        Gson gson = new Gson();
        String[] msges = new String[result.size()];
        int i = 0;
        for(Map<String,String> m : result){
            msges[i++] = gson.toJson(m);
        }
        return msges;
    }
    

    @Override
    public FileJsonArrayList getFileMessage(TestPluginCallBack callBack) {
    	
        FileJsonArrayListPlus result = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
        System.out.println("GetFileMessage ..............");
        logger.info("start getFileMessage......");
        Gson gson = new Gson();

        if(this.showProgress) {
	        new Thread(()->{
	          probressBar.start();
	        }).start();
        }

        total = 1;
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            logger.info("count calculate -> total : {} , values.size : {}", total, factor.getFactorRange().getValues().size());
            total = total * factor.getFactorRange().getValues().size();
        }
        logger.info("expected total test data count : {}" , total);


        int varIdx = 0;
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            logger.info("total test data count : {}" , varIdx);
            List<String> rangeValueList = factor.getFactorRange().getValues();
            FileJsonArrayListPlus tmp = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
            if(result.size() == 0){
                logger.info("Result size is Zero , so add {}" , rangeValueList.size());
                for(int x = 0; x < rangeValueList.size(); x++) {
                    tmp.add(new HashMap<>());
                }
            } else{
                logger.info("Result size is not Zero , so add {}" , rangeValueList.size()*result.size());
                logger.info("Result size : {} ,  Range size: {}" , result.size() , rangeValueList.size());
                for(int x = 0; x < rangeValueList.size(); x++) {
                    result.forEach(m ->{
                        tmp.write(m);
                    });
                }
            } 
            //int start = 1;
            FileJsonArrayListPlus tmp2 = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
            tmp.forEach(s->{
                for (int rIdx = 0; rIdx < rangeValueList.size() ; rIdx++) {
                    Map<String,String> m = gson.fromJson(s,Map.class);
                    if(TestPluginConstants.ta_random_key_data.equals(rangeValueList.get(rIdx))) {
                        // m.put(factor.getName(), TestPluginMessageFactorImplFactory.getRandomCharacter(rangeValueList.get(rIdx).length()).toString());
                        m.put(factor.getName(), TestPluginMessageFactorImplFactory.getRandomCharacter(Integer.parseInt(factor.getLength())).toString());
                    }else{
                        m.put(factor.getName(), rangeValueList.get(rIdx));
                    }
                    tmp2.add(m);
                }
            });
            result.clear();
            result = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
            result.writeAll(tmp2);
            tmp2.clear();
            tmp.clear();
            progress = (int)Math.round(((varIdx*1.0)/(total*1.0))*100);
            logger.info("progress status : {} / {} / {} " , progress,varIdx, total);
            varIdx += result.size();
            callBack.call(varIdx+"/"+total, total);
        }
        logger.info("Current message count is {}",result.size());
        for(int i = 1; i <= result.size() && i < 10 ; i++){
            logger.info("Forward Sample["+i+"] message is {}",result.get(i));
        }
        for(int i = result.size() ; i > 0 && i > (result.size()-10) ; i--){
            logger.info("Backward Sample["+i+"] message is {}",result.get(i));
        }
        if(this.showProgress) probressBar.dispose();
        callBack.call(varIdx+"/"+total + " , completed.", varIdx);
        return result;
    }

    public FileJsonArrayList getFileMessage(TestPluginCallBack callBack , boolean show) {
    	this.showProgress = show;
    	return getFileMessage(callBack);
    }
    
}
