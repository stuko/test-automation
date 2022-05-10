package com.auto.test.jmeter.plugin.common.factor.value;

import com.auto.test.jmeter.plugin.common.util.SecurityUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.auto.test.jmeter.plugin.common.factor.define.TestPluginMessageFactor;
import com.auto.test.jmeter.plugin.common.factor.define.TestPluginMessageSubstringFactor;

public class TestPluginMessageFactorDatetimeValue  extends AbstractTestPluginMessageFactorValueImpl{
    static Logger logger = LoggerFactory.getLogger(TestPluginMessageFactorDatetimeValue.class);

    public static enum RangeType{Gap, FromTo};
    public static enum Unit{Second, Minute, Hour, Day, Month, Year};
    public static enum Format{YYYY,MM,DD,ss,mm,HH,HHmmss,YYYYMM,YYYYMMDD,YYYYMMDDHHmmss, YYYYMMDDHH, YYYYMMDDHHmm };
    private RangeType rangeType;
    private Unit unit;
    private String from;
    private String to;
    private Format format;
    private int gap;
    private int count;

    public TestPluginMessageFactorDatetimeValue(int gap , Unit unit , int count , Format format){
        this.rangeType = RangeType.Gap;
        this.gap = gap;
        this.unit = unit;
        this.count = count;
        this.format = format;
    }
    public TestPluginMessageFactorDatetimeValue(String from, String to, int count, Format format){
        this.rangeType = RangeType.FromTo;
        this.setFrom(from);
        this.setTo(to);
        this.count = count;
        this.setFormat(format);
    }
    public TestPluginMessageFactorDatetimeValue(String from, String to, Unit unit, int count, Format format){
        this.rangeType = RangeType.FromTo;
        this.setFrom(from);
        this.setTo(to);
        this.unit = unit;
        this.count = count;
        this.setFormat(format);
    }

    private String getDateTime(int gap, Unit unit , String format){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        String date = this.getDateTime(cal,gap,unit,format);
        if(this.getTaPluginMessageFactor().isSubstring()){
            date = date.substring(this.getTaPluginMessageFactor().getSubstring_first_index(),this.getTaPluginMessageFactor().getSubstring_second_index());
        }
        return date;
    }

    private String getDateTime(Calendar cal ,int gap, Unit unit , String format){
        SimpleDateFormat dtFormat = new SimpleDateFormat(format);
        if(unit == null) unit = Unit.Hour;
        if(unit.equals(Unit.Second))cal.add(Calendar.SECOND, gap);
        else if(unit.equals(Unit.Minute))cal.add(Calendar.MINUTE, gap);
        else if(unit.equals(Unit.Hour))cal.add(Calendar.HOUR, gap);
        else if(unit.equals(Unit.Day))cal.add(Calendar.DATE, gap);
        else if(unit.equals(Unit.Month))cal.add(Calendar.MONTH, gap);
        else if(unit.equals(Unit.Year))cal.add(Calendar.YEAR, gap);
        return dtFormat.format(cal.getTime());
    }

    private String format(){
        if(Format.YYYY.equals(this.format)) return "yyyy";
        else if(Format.MM.equals(this.format)) return "MM";
        else if(Format.DD.equals(this.format)) return "dd";
        else if(Format.HH.equals(this.format)) return "HH";
        else if(Format.mm.equals(this.format)) return "mm";
        else if(Format.ss.equals(this.format)) return "ss";
        else if(Format.HHmmss.equals(this.format)) return "HHmmss";
        else if(Format.YYYYMM.equals(this.format)) return "yyyyMM";
        else if(Format.YYYYMMDD.equals(this.format)) return "yyyyMMdd";
        else if(Format.YYYYMMDDHH.equals(this.format)) return "yyyyMMddHH";
        else if(Format.YYYYMMDDHHmm.equals(this.format)) return "yyyyMMddHHmm";
        else if(Format.YYYYMMDDHHmmss.equals(this.format)) return "yyyyMMddHHmmss";
        else  return "yyyyMMdd";
    }

    @Override
    public List<String> getValues() {
        List<String> result = new ArrayList<>();
        if(this.getRangeType().equals(RangeType.Gap)){
            //for(int i = 0; i < count; i++)
            if(this.getTaPluginMessageFactor().isEncode()){
                result.add(SecurityUtil.encode(this.getDateTime(this.getGap(),this.getUnit(), this.format())));
            }else result.add(this.getDateTime(this.getGap(),this.getUnit(), this.format()));
        }else if(this.getRangeType().equals(RangeType.FromTo)){
            SimpleDateFormat sdf = new SimpleDateFormat(this.format());
            try {
                try{
                    if(this.getFrom().length() != this.format().length() && this.getTo().length() != this.format().length()){
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());
                        String from_date = this.getDateTime(cal,Integer.parseInt(this.getFrom()),unit,this.format());
                        String to_date = this.getDateTime(cal,Integer.parseInt(this.getTo()),unit,this.format());
                        this.setFrom(from_date);
                        this.setTo(to_date);
                    }
                }catch(Exception ee){
                    logger.error(ee.toString());
                    logger.info("Skip exception , just use....");
                }
                Date df = sdf.parse(this.getFrom());
                Date dt = sdf.parse(this.getTo());
                long diff = dt.getTime() - df.getTime();
                Calendar cal = Calendar.getInstance();
                for(int i = 0; i < count; i++) {
                    cal.setTime(df);
                    int r = (int) (Math.random() * (diff/(1000)));
                    if(this.getTaPluginMessageFactor().isEncode()){
                        result.add(SecurityUtil.encode(this.getDateTime(cal, r, Unit.Second, this.format())));
                    }else result.add(this.getDateTime(cal, r, Unit.Second, this.format()));
                }
            }catch(Exception e){
                logger.error(e.toString(),e);
                if(this.getTaPluginMessageFactor().isEncode()){
                    result.add(SecurityUtil.encode(this.getDateTime(this.getGap(),this.getUnit(), this.format())));
                }else result.add(this.getDateTime(this.getGap(),this.getUnit(), this.format()));
            }
        }
        return result;
    }

    @Override
    public String toString(){
        if(this.getRangeType().equals(RangeType.Gap)){
            //for(int i = 0; i < count; i++)
            if(this.getTaPluginMessageFactor().isEncode()){
                return SecurityUtil.encode(this.getDateTime(this.getGap(),this.getUnit(), this.format()));
            }else return this.getDateTime(this.getGap(),this.getUnit(), this.format());
        }else if(this.getRangeType().equals(RangeType.FromTo)) {
            SimpleDateFormat sdf = new SimpleDateFormat(this.format());
            try {
                try {
                    if (this.getFrom().length() != this.format().length() && this.getTo().length() != this.format().length()) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());
                        String from_date = this.getDateTime(cal, Integer.parseInt(this.getFrom()), unit, this.format());
                        String to_date = this.getDateTime(cal, Integer.parseInt(this.getTo()), unit, this.format());
                        this.setFrom(from_date);
                        this.setTo(to_date);
                    }
                } catch (Exception ee) {
                    logger.error(ee.toString());
                    logger.info("Skip exception , just use....");
                }
                Date df = sdf.parse(this.getFrom());
                Date dt = sdf.parse(this.getTo());
                long diff = dt.getTime() - df.getTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(df);
                int r = (int) (Math.random() * (diff / (1000)));
                if (this.getTaPluginMessageFactor().isEncode())
                    return SecurityUtil.encode(this.getDateTime(cal, r, Unit.Second, this.format()));
                else return this.getDateTime(cal, r, Unit.Second, this.format());
            } catch(Exception e){
                logger.error(e.toString(), e);
                if (this.getTaPluginMessageFactor().isEncode()) {
                    return SecurityUtil.encode(this.getDateTime(this.getGap(), this.getUnit(), this.format()));
                } else return this.getDateTime(this.getGap(), this.getUnit(), this.format());
            }
        }
        return null;
    }

    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public Format getFormat() {
        return format;
    }
    public void setFormat(Format format) {
        this.format = format;
    }
    public RangeType getRangeType() {
        return rangeType;
    }
    public void setRangeType(RangeType rangeType) {
        this.rangeType = rangeType;
    }
    public int getGap() {
        return gap;
    }
    public void setGap(int gap) {
        gap = gap;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

}
