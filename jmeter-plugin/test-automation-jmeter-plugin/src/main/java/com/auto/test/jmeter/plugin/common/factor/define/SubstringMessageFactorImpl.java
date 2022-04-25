package com.auto.test.jmeter.plugin.common.factor.define;

import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.auto.test.jmeter.plugin.common.util.TestPluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubstringMessageFactorImpl extends BasicMessageFactorImpl implements TestPluginMessageSubstringFactor {

    static Logger logger = LoggerFactory.getLogger(SubstringMessageFactorImpl.class);

    private boolean isSubstring = false;
    private int substring_first_index = -1;
    private int substring_second_index = -1;

    public SubstringMessageFactorImpl(String n, String t, String v, int c, boolean encode){
        super(n,t,v,c,encode);
    }

    public SubstringMessageFactorImpl(String n, String t, String v, boolean encode){
        super(n,t,v,encode);
    }

    public boolean isSubstring() {
        return isSubstring;
    }

    public void setSubstring(boolean substring) {
        isSubstring = substring;
    }

    public int getSubstring_first_index() {
        return substring_first_index;
    }

    public void setSubstring_first_index(int substring_first_index) {
        this.substring_first_index = substring_first_index;
    }

    public int getSubstring_second_index() {
        return substring_second_index;
    }

    public void setSubstring_second_index(int substring_second_index) {
        this.substring_second_index = substring_second_index;
    }

    public void checkSubstring(){
        if(this.getValue().trim().startsWith(TestPluginConstants.ta_substring_data)){
            if(this.getValue().trim().indexOf(" ") > 0) {
                String[] substrs = this.getValue().trim().split(" ");
                if(substrs.length == 3) {
                    String fun = substrs[0];
                    String from = substrs[1];
                    String to = substrs[2];
                    if(from.startsWith(TestPluginConstants.ta_substring_from)
                            && to.startsWith(TestPluginConstants.ta_substring_to)){
                        String data = TestPluginUtil.getSubstringData(fun);
                        from = TestPluginUtil.getSubstringData(from);
                        to = TestPluginUtil.getSubstringData(to);
                        logger.info("data = {}", data);
                        logger.info("from = {}", from);
                        logger.info("to = {}", to);
                        this.isSubstring = true;
                        this.substring_first_index = Integer.parseInt(from);
                        this.substring_second_index = Integer.parseInt(to);
                        this.setValue(data);
                    }
                }else if(substrs.length == 2) {
                    String fun = substrs[0];
                    String from = substrs[1];
                    String data = TestPluginUtil.getSubstringData(fun);
                    from = TestPluginUtil.getSubstringData(from);
                    if(from.startsWith(TestPluginConstants.ta_substring_from)){
                        logger.info("data = {}", data);
                        logger.info("from = {}", from);
                        this.isSubstring = true;
                        this.substring_first_index = Integer.parseInt(from);
                        this.setValue(data);
                    }
                }else{
                    logger.error("Can not substring.... error {}" , this.getValue());
                }
            }
        }
    }
}
