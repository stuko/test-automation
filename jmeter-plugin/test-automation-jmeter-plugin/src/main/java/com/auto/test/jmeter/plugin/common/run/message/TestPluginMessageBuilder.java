package com.auto.test.jmeter.plugin.common.run.message;


import com.auto.test.jmeter.plugin.common.data.FileJsonArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;

public abstract class TestPluginMessageBuilder {

    static Logger logger = LoggerFactory.getLogger(TestPluginMessageBuilder.class);
    public TestPluginMessage message = new TestPluginMessage();

    public void build(String[][] datas){
        this.message.clear();
        if(datas != null && datas[0] != null) {
            for (String[] rows : datas) {
                if (rows.length == 5) {
                    logger.info("name={}, type={}, value={}, length={} , encode={}", rows[0], rows[1], rows[2], rows[3], rows[4]);
                    this.message.addBody(rows[0], rows[1], rows[2], "Y".equals(rows[4]) ? true:false ).setLength(rows[3]);
                } else if (rows.length == 6) {
                    logger.info("name={}, type={}, value={}, count={}, length={} , encode={}", rows[0], rows[1], rows[2], rows[3], rows[4], rows[5]);
                    this.message.addBody(rows[0], rows[1], rows[2], Integer.parseInt(rows[3]),  "Y".equals(rows[5]) ? true:false).setLength(rows[4]);
                } else {
                    logger.info("data error................");
                }
            }
        }
    }
    abstract public FileJsonArrayList getFileMessage(TestPluginCallBack callBack);

}
