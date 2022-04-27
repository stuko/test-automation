package com.auto.test.jmeter.plugin.common.factor;

import com.auto.test.jmeter.plugin.common.factor.define.SubstringMessageFactorImpl;
import com.auto.test.jmeter.plugin.common.factor.range.TestPluginMessageFactorRangeCollection;
import com.auto.test.jmeter.plugin.common.factor.value.TestPluginMessageFactorFileValue;
import com.auto.test.jmeter.plugin.common.factor.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPluginMessageFactorImplFactory extends SubstringMessageFactorImpl {

    static Logger logger = LoggerFactory.getLogger(TestPluginMessageFactorImplFactory.class);
    public TestPluginMessageFactorImplFactory(String n, String t, String v, int c, boolean encode){
        super(n,t,v,c,encode);
    }
    public TestPluginMessageFactorImplFactory(String n, String t, String v, boolean encode){
        super(n,t,v,encode);
    }

    public TestPluginMessageFactorRangeCollection getFactorRange(){

        TestPluginMessageFactorRangeCollection range = new TestPluginMessageFactorRangeCollection();
        this.checkSubstring();
        if(this.getType() == null){
            return this.getNA(range);
        }else if(this.getType().trim().equalsIgnoreCase("string")){
            range = this.getCharacterRange(range);
            return range;
        }else if(this.getType().trim().equalsIgnoreCase("number")){
            range = this.getNumberRange(range);
            return range;
        }else if(this.getType().trim().equalsIgnoreCase("datetime")){
            // 1형식:  20210301~20210405,yyyymmdd
            // 2형식: -20,day,yyyymmdd
            range = this.getDateTimeRange(range);
            return range;
        }else if(this.getType().trim().equalsIgnoreCase("key")) {
            range = this.getKeyRange(range);
            return range;
        }else if(this.getType().trim().equalsIgnoreCase("reference")) {
            range = this.getReferenceRange(range);
            return range;
        }else if(this.getType().trim().equalsIgnoreCase("file")) {
            TestPluginMessageFactorFileValue file = new TestPluginMessageFactorFileValue(this);
            String source = file.toString();
            TestPluginMessageFactorCharacterValue f = new TestPluginMessageFactorCharacterValue(source);
            f.setTaPluginMessageFactor(this);
            range.getRanges().put(this.getName(), f);
            return range;
        }else{
            range = this.getCharacterRange(range);
            return range;
        }
    }



    private TestPluginMessageFactorRangeCollection getDateTimeRange(TestPluginMessageFactorRangeCollection range) {
        String[] data = this.getValue().trim().split("[|]");
        int i = 0;
        for(String d : data){
            // logger.info("Datetime range[{}] : {}",i,d);
            i++;
            String[] fdata = d.split(",");
            String period = fdata[0].trim(); // X ~ Y or 10 or -10, 34...
            if(period.indexOf("~") > 0){
                String unit = fdata[1].trim();   // second,minute,hour,day,month,year
                String format = fdata[2].trim(); // yyyyMMdd, yyyyMMddHH, yyyyMMddHHmm, yyyyMMddHHmmss
                String[] pds = period.split("~");
                TestPluginMessageFactorDatetimeValue f = new TestPluginMessageFactorDatetimeValue(pds[0],pds[1],this.getUnit(unit),this.getCount(),this.getFormat(format));
                f.setTaPluginMessageFactor(this);
                range.getRanges().put(this.getName()+"-"+i,f);
            }else{
                String unit = fdata[1].trim();   // second,minute,hour,day,month,year
                String format = fdata[2].trim(); // yyyyMMdd, yyyyMMddHH, yyyyMMddHHmm, yyyyMMddHHmmss
                TestPluginMessageFactorDatetimeValue f = new TestPluginMessageFactorDatetimeValue(Integer.parseInt(period),this.getUnit(unit),this.getCount(),this.getFormat(format));
                f.setTaPluginMessageFactor(this);
                range.getRanges().put(this.getName()+"-"+i,f);
            }
        }
        return range;
    }

    private TestPluginMessageFactorRangeCollection getNumberRange(TestPluginMessageFactorRangeCollection range) {
        String[] data = this.getValue().trim().split("[|]");
        int i = 0;
        for(String d : data){
            // logger.info("Number range[{}] : {}",i,d);
            i++;
            String[] fdata = d.split("~");
            long from = Long.parseLong(fdata[0].trim());
            long to = Long.parseLong(fdata[1].trim());
            TestPluginMessageFactorNumberValue f = new TestPluginMessageFactorNumberValue(from,to,this.getCount());
            f.setTaPluginMessageFactor(this);
            range.getRanges().put(this.getName()+"-"+i,f);
        }
        return range;
    }

    private TestPluginMessageFactorRangeCollection getKeyRange(TestPluginMessageFactorRangeCollection range) {
        String[] data = this.getValue().trim().split("[|]");
        int i = 0;
        for(String d : data){
            // logger.info("Key range[{}] : {}",i,d);
            i++;

            String[] fdata = d.split(",");
            if(fdata.length == 3) {
                String prefix = fdata[0];
                String len = fdata[1];
                String postfix = fdata[2];
                int size = Integer.parseInt(len);
                // String[] rst = new String[1];
                // rst[0] = prefix + getRandomCharacter(size) + postfix;
                // FdsPluginMessageFactorCharacter f = new FdsPluginMessageFactorCharacter(rst);
                TestPluginMessageFactorKeyValue f = new TestPluginMessageFactorKeyValue(size,prefix,postfix);
                f.setTaPluginMessageFactor(this);
                range.getRanges().put(this.getName() + "-" + i, f);
            }else if(fdata.length == 1){
                String len = fdata[0];
                int size = Integer.parseInt(len);
                // StringBuilder sb = getRandomCharacter(size);
                // String[] rst = new String[1];
                // rst[0] = sb.toString();
                TestPluginMessageFactorKeyValue f = new TestPluginMessageFactorKeyValue(size);
                f.setTaPluginMessageFactor(this);
                range.getRanges().put(this.getName() + "-" + i, f);
            }
        }
        return range;
    }

    private TestPluginMessageFactorRangeCollection getReferenceRange(TestPluginMessageFactorRangeCollection range) {
        String[] data = this.getValue().trim().split("[|]");
        int i = 0;
        for(String d : data){
            i++;
            // addr|addr2|addr3 --> map.get("addr"),map.get("addr2"),map.get("addr3") 와 같은 의미
            // @SUBSTR(addr) FROM(0) TO(20) 이면 addr에서 substring(0,20)을 해줘야 함.
            // 아직 map의 값이 생성되기 전이면, 해당 값을 가져 올 수가 없게됨.
            // TestMessage 클래스에서 데이터를 Executor에 넘겨 줄 때, Write Line을 하기 전에 값을 읽어와서, 저장 해 주어야 함.
            // reference 타입인 정보를 넣기 위해서는 reference 타입인 변수들을 ReferenceMap에 넣고, ReferenceMap에 target 값을 넣고,
            // ReferenceMap을 Write Line 할 Map에 putAll 해줘야 함.
            // 만약 SUBSTRING이 적용 되어야 하면, putAll 하기 전에 substring을 해줘야 함.
            TestPluginMessageFactorReferenceValue f = new TestPluginMessageFactorReferenceValue(d);
            f.setTaPluginMessageFactor(this);
            range.getRanges().put(this.getName() + "-" + i, f);
        }
        return range;
    }

    public static StringBuilder getRandomCharacter(int size) {
        StringBuilder sb = new StringBuilder(size);
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        for (int x = 0; x < size; x++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int)(AlphaNumericString.length() * Math.random());
            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb;
    }

    private TestPluginMessageFactorRangeCollection getNA(TestPluginMessageFactorRangeCollection range){
        TestPluginMessageFactorCharacterValue f = new TestPluginMessageFactorCharacterValue("N/A");
        f.setTaPluginMessageFactor(this);
        range.getRanges().put(this.getName(),f);
        return range;
    }

    private TestPluginMessageFactorRangeCollection getCharacterRange(TestPluginMessageFactorRangeCollection range) {
        if(this.getValue() == null){
            range = getNA(range);
        }else if("".equals(this.getValue().trim())){
            if(!"".equals(this.getLength())){
                try {
                    int len = Integer.parseInt(this.getLength());
                    String fdata = getRandomCharacter(len).toString();
                    TestPluginMessageFactorCharacterValue f = new TestPluginMessageFactorCharacterValue(fdata);
                    f.setTaPluginMessageFactor(this);
                    range.getRanges().put(this.getName(), f);
                }catch(Exception e){
                    logger.error(e.toString(),e);
                    range = getNA(range);
                }
            }else{
                range = getNA(range);
            }
        }else {
            String[] data = this.getValue().trim().split("[|]");
            int i = 0;
            for (String d : data) {
                // logger.info("Character range[{}] : {}",i,d);
                i++;
                String[] fdata = d.split(",");
                TestPluginMessageFactorCharacterValue f = new TestPluginMessageFactorCharacterValue(fdata);
                f.setTaPluginMessageFactor(this);
                range.getRanges().put(this.getName() + "-" + i, f);
            }
        }
        return range;
    }

    public TestPluginMessageFactorDatetimeValue.Unit getUnit(String u){
        if(u.trim().equalsIgnoreCase("second")) return TestPluginMessageFactorDatetimeValue.Unit.Second;
        else if(u.trim().equalsIgnoreCase("minute")) return TestPluginMessageFactorDatetimeValue.Unit.Minute;
        else if(u.trim().equalsIgnoreCase("hour")) return TestPluginMessageFactorDatetimeValue.Unit.Hour;
        else if(u.trim().equalsIgnoreCase("day")) return TestPluginMessageFactorDatetimeValue.Unit.Day;
        else if(u.trim().equalsIgnoreCase("month")) return TestPluginMessageFactorDatetimeValue.Unit.Month;
        else if(u.trim().equalsIgnoreCase("year")) return TestPluginMessageFactorDatetimeValue.Unit.Year;
        else return TestPluginMessageFactorDatetimeValue.Unit.Day;
    }

    // YYYY,MM,DD,ss,mm,HH,HHmmss,YYYYMM,YYYYMMDD,YYYYMMDDHHmmss, YYYYMMDDHH, YYYYMMDDHHmm
    public TestPluginMessageFactorDatetimeValue.Format getFormat(String f){
        if(f.trim().equalsIgnoreCase("yyyy")) return TestPluginMessageFactorDatetimeValue.Format.YYYY;
        else if(f.trim().equals("MM")) return TestPluginMessageFactorDatetimeValue.Format.MM;
        else if(f.trim().equalsIgnoreCase("dd")) return TestPluginMessageFactorDatetimeValue.Format.DD;
        else if(f.trim().equalsIgnoreCase("hh")) return TestPluginMessageFactorDatetimeValue.Format.HH;
        else if(f.trim().equals("mm")) return TestPluginMessageFactorDatetimeValue.Format.mm;
        else if(f.trim().equalsIgnoreCase("ss")) return TestPluginMessageFactorDatetimeValue.Format.ss;
        else if(f.trim().equalsIgnoreCase("hhmmss")) return TestPluginMessageFactorDatetimeValue.Format.HHmmss;
        else if(f.trim().equalsIgnoreCase("yyyymm")) return TestPluginMessageFactorDatetimeValue.Format.YYYYMM;
        else if(f.trim().equalsIgnoreCase("yyyymmdd")) return TestPluginMessageFactorDatetimeValue.Format.YYYYMMDD;
        else if(f.trim().equalsIgnoreCase("yyyymmddhh")) return TestPluginMessageFactorDatetimeValue.Format.YYYYMMDDHH;
        else if(f.trim().equalsIgnoreCase("yyyymmddhhmm")) return TestPluginMessageFactorDatetimeValue.Format.YYYYMMDDHHmm;
        else if(f.trim().equalsIgnoreCase("yyyymmddhhmmss")) return TestPluginMessageFactorDatetimeValue.Format.YYYYMMDDHHmmss;
        else  return TestPluginMessageFactorDatetimeValue.Format.YYYYMMDD;
    }


}