package com.auto.test.jmeter.plugin.common.run.message;

import com.google.gson.Gson;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayList;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListPlus;
import com.auto.test.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.auto.test.jmeter.plugin.common.gui.PluginProgressBar;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
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

    AtomicBoolean stop = new AtomicBoolean(false);

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
        // 미리 해당 항목수 만큼은 Map을 입력해 놓는다.
        // for(int i = 0; i < this.message.getBodys().size(); i++) result.add(new HashMap<>());
        // 3개
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            logger.info("loop bodys {}" , varIdx);
            List<String> rangeValueList = factor.getFactorRange().getValues();
            List<Map<String,String>>  tmp = new ArrayList<>();
            // Range 범위 만큼 Map 을 복제 해서 넣어 준다.

            //result.forEach(m->{
            //    logger.info("before factorName : {} , {}",factor.getName(), m );
            //});

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
            //logger.info("Range Value List is {}" , rangeValueList);


            int start = 0;

            //tmp.forEach(m->{
            //    logger.info("before factorName : {} , {}",factor.getName(), m );
            //});

            for(; start < tmp.size(); ) {
                for (int rIdx = 0; rIdx < rangeValueList.size() ; rIdx++) {
                    // logger.info("before factorName : {} , {} : {}",factor.getName(), start, tmp.get(start));
                    //logger.info("########## Factor({}) value[{}] : {} " , factor.getName(),start, tmp.get(start).get(factor.getName()));
                    tmp.get(start).put(factor.getName(), rangeValueList.get(rIdx));
                    //logger.info("########## range value : {} " , rangeValueList.get(rIdx));
                    // logger.info("after factorName : {} , {} : {}",factor.getName(), start, tmp.get(start));
                    start++;
                }
            }
            //logger.info("Current tmp message count is {}",tmp.size());
            //for(int i = 0; i < tmp.size() && i < 10 ; i++){
            //    logger.info("Forward tmp Sample["+i+"] message is {}",tmp.get(i));
            //}
            //for(int i = tmp.size()-1 ; i >= 0 && i > (tmp.size()-10) ; i--){
            //    logger.info("Backward tmp Sample["+i+"] message is {}",tmp.get(i));
            //}

            result = new ArrayList<>();
            result.addAll(tmp);
            progress += result.size();
            callBack.call(progress+"" , progress);
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
    

    /*
    < Init >
    1. 항목의 갯수 만큼 Queue를 만든다.
    2. Queue들에는 순서가 존재 함 Queue들을 Array 에 넣는다.
    3. Queue 탐색 인덱스를 0으로 정한다.
    4. 탐색 인덱스보다 작거나 같은 Queue들은 탐색 / 추출 대상 Queue이다,
    5. 나머지 Queue들은 계속 같은
    < Execute >
    1. 최초에 각 Queue에서 값을 하나씩 Poll 한다
    2. 가
     */

    @Override
    public FileJsonArrayList getFileMessage(TestPluginCallBack callBack) {
        // List<Map<String,String>> result = new ArrayList<>();
        FileJsonArrayListPlus result = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
        System.out.println("GetFileMessage ..............");
        logger.info("start getFileMessage......");
        Gson gson = new Gson();

        PluginProgressBar probressBar = new PluginProgressBar(100,1 , 1000, ()->{
            return getProgress();
        });
        new Thread(()->{
          probressBar.start();
        }).start();

        total = 1;
        // 메시지의 각 항목들의 조합으로 만들어 낼 수 있는 테스트 데이터 갯수를 찾아 낸다.
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            logger.info("count calculate -> total : {} , values.size : {}", total, factor.getFactorRange().getValues().size());
            total = total * factor.getFactorRange().getValues().size();
        }
        logger.info("expected total test data count : {}" , total);


        int varIdx = 0;
        // 메시지의 각 항목들을 Loop
        for(TestPluginMessageFactorImplFactory factor: this.message.getBodys()){
            logger.info("total test data count : {}" , varIdx);
            List<String> rangeValueList = factor.getFactorRange().getValues();
            FileJsonArrayListPlus tmp = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
            // 이미 생성한 결과가 없으면, 신규 생성
            if(result.size() == 0){
                logger.info("Result size is Zero , so add {}" , rangeValueList.size());
                for(int x = 0; x < rangeValueList.size(); x++) {
                    tmp.add(new HashMap<>());
                }
            // 이미 생성한 결과가 있으면 결과를 tmp 리스트 맵에 복사해 줌.
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
            // 기존에 생성한 결과를 Loop
            tmp.forEach(s->{
                // 항목의 범위 만큼 Loop
                for (int rIdx = 0; rIdx < rangeValueList.size() ; rIdx++) {
                    // logger.info("########## range value : {} " , rangeValueList.get(rIdx));
                    // logger.info("########## find in tmp , idx : {} in {}" , start, tmp.size());
                    // Map<String,String> m = tmp.get(start);
                    // logger.info("########## tmp value : {} " , m);
                    Map<String,String> m = gson.fromJson(s,Map.class);
                    if(TestPluginConstants.ta_random_key_data.equals(rangeValueList.get(rIdx))) {
                        m.put(factor.getName(), TestPluginMessageFactorImplFactory.getRandomCharacter(rangeValueList.get(rIdx).length()).toString());
                        // System.out.println("RAND : " + rangeValueList.get(rIdx));
                    }else{
                        m.put(factor.getName(), rangeValueList.get(rIdx));
                        // System.out.println("NOT RAND : " + rangeValueList.get(rIdx));
                    }
                    // 기존의 결과와 항목의 범위 만큼 조합을 만들어 새로운 tmp2에 저장함.
                    tmp2.add(m);
                    // start++;
                }
            });
            // 결과 리스트를 신규 생성함.
            result.clear();
            result = new FileJsonArrayListPlus(TestPluginConstants.ta_data_path);
            // 결과 리스트에 tmp2의 정보를 모두 복사함.
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
        probressBar.dispose();
        callBack.call(varIdx+"/"+total + " , completed.", varIdx);
        return result;
    }

}
