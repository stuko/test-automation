package com.kcb.fds.jmeter.plugin.common.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FdsKafkaPluginGui extends AbstractFdsPluginGui {
    
    static Logger logger = LoggerFactory.getLogger(FdsKafkaPluginGui.class);
   
    public FdsKafkaPluginGui(){
        super();
        this.getTestRunConfigPanel().setConnectionText(this.getDefaultText());
    }

    public String getDefaultText() {
        String kafka = "{\n" +
                "\"server\":\"192.168.57.252:9092,192.168.57.253:9092,192.168.57.254:9092\"\n" +
                ",\"topic\":\"fds-bank.t\"\n" +
                "}";
        String http = "{\n" +
                "\"url\":\"http://1.1.1.1:3000\"\n"+
                "}";
        String tcp = "{\n" +
                "\"ip\":\"1.1.1.1\"\n"+
                ",\"port\":\"3000\"\n" +
                "}";
        return kafka;
    }

    @Override
    public String getLabelResource() {
        return "FDS Kafka 테스트 데이터 플러그인";
    }

    @Override
    public void display() {
        super.display();
    }

}
