package com.auto.test.jmeter.plugin.common.config;

import com.auto.test.jmeter.plugin.common.data.FileJsonArrayList;
import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.google.gson.Gson;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

@GUIMenuSortOrder(1)
public class TestAutomationConfigTestElement extends ConfigTestElement implements TestBean, LoopIterationListener {

    static Logger logger = LoggerFactory.getLogger(TestAutomationConfigTestElement.class);

    Gson gson = new Gson();
    private String filename;

    @Override
    public void iterationStart(LoopIterationEvent iterEvent) {

        final JMeterContext context = getThreadContext();
        JMeterVariables threadVars = context.getVariables();
        try {
            logger.info("######## file name : {} " , this.getFilename());
            FileJsonArrayListQueue fjalq = FileJsonArrayListQueue.getInstance(this.getFilename());
            String json = fjalq.next();
            if(json != null) {
                Map<String, Object> map = gson.fromJson(json, Map.class);
                map.forEach((k, v) -> {
                    threadVars.put(k, (String) v);
                });
            }
        } catch (Exception e) { // treat the same as EOF
            logger.error(e.toString());
        }
    }

    @Override
    public void removed() {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
