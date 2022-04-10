package com.kcb.fds.jmeter.plugin.common.factor.define;

public class BasicMessageFactorImpl implements TestPluginMessageFactor {

    private String name;
    private String value;
    private String type;
    private String length;
    private boolean encode;
    private int count;

    public BasicMessageFactorImpl(String n, String t, String v, int c, boolean encode){
        this.setName(deleteQuot(n));
        this.setType(deleteQuot(t));
        this.setValue(deleteQuot(v));
        this.setCount(c);
        this.setEncode(encode);
    }

    public BasicMessageFactorImpl(String n, String t, String v, boolean encode){
        this.setName(deleteQuot(n));
        this.setType(deleteQuot(t));
        this.setValue(deleteQuot(v));
        this.setEncode(encode);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() { return value;}
    public void setValue(String value) {this.value = value;}
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getLength() {
        return length;
    }
    public void setLength(String length) {
        this.length = length;
    }
    public boolean isEncode() {
        return encode;
    }
    public void setEncode(boolean encode) {
        this.encode = encode;
    }
    public String deleteQuot(String d){
        return d.replaceAll("\"","");
    }
    public String getLengthString(String source, int len){
        if(source.length() >= len){
            return source.substring(0,len);
        }else{
            int max = len - source.length();
            for(int i = 0; i < max; i++){
                source = source + " ";
            }
            return source;
        }
    }

}
