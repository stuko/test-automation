/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kcb.fds.jmeter.plugin.common.gui;

import com.google.gson.Gson;
import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.kcb.fds.jmeter.plugin.common.data.TestPluginTestData;
import com.kcb.fds.jmeter.plugin.common.data.TestPluginTestDataQueueImpl;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginConstants;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import com.kcb.fds.jmeter.plugin.common.function.TestPluginCallBack;
import com.kcb.fds.jmeter.plugin.common.run.executor.AbstractPluginExecutor;
import com.kcb.fds.jmeter.plugin.common.run.executor.ExecutorMap;
import com.kcb.fds.jmeter.plugin.common.run.executor.TestPluginExecutor;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author O218001_D
 */
public class TestRunConfigPanel extends PluginGridPanel{
    
    static Logger logger = LoggerFactory.getLogger(TestRunConfigPanel.class);
    
    public JTextArea textArea = null;
    Gson gson = new Gson();
    TestDataConfigPanel testDataConfigPanel;
    JComboBox<String> combo;
    JButton save;
    String[] method = {"KAFKA","REST"};
    
    public TestRunConfigPanel(TestDataConfigPanel panel, TestPluginCallBack callback){
        this.testDataConfigPanel = panel;
        this.initComponent(this.testDataConfigPanel);
    }
    
    public void loadTestRun(Map<String,Object> run){
        try{
            if(run == null || run.size() == 0) return;
            this.combo.setSelectedItem(run.get("type"));
            if(!"KAFKA".equals(run.get("type"))){
               run.remove("topic");
            }
            run.remove("type");
           this.textArea.setText(gson.toJson(run));
        }catch(Exception e){
            logger.error(e.toString(), e);
        }
    }
    
    public void initComponent(TestDataConfigPanel panel){
        this.initComponent(panel, null);
    }
    public void initComponent(TestDataConfigPanel panel, TestPluginCallBack callback){
        this.setLayout(new GridBagLayout());
        GridBagConstraints labelConstraints = this.getLabelGBC();
        GridBagConstraints editConstraints = this.getEditGBC();
        labelConstraints.insets = new Insets(10, 10, 2, 4);
        editConstraints.insets = new Insets(10, 10, 2, 4);
        
        JLabel header =  new JLabel("테스트 실행 설정", JLabel.LEFT);
        header.setFont(new java.awt.Font("맑은 고딕", 1, 18));
        this.add(this,0,0,1,1, GridBagConstraints.WEST,GridBagConstraints.NONE, editConstraints, header);
        
        combo = new JComboBox<String>(method);
        this.add(this,0,1,1,1, GridBagConstraints.WEST,GridBagConstraints.NONE, editConstraints, combo);
        
        textArea = new JTextArea();  //JTextArea 생성
        textArea.setRows(5);
        textArea.setBounds(50, 50, 300, 300); //JTeatArea 크기 및 위치 지정
        textArea.setEditable(true); //실행시 JtextArea edit 금지 (글을 쓸 수 없음) true면 가능
        textArea.setText(this.getDefaultText());

        javax.swing.JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new java.awt.Dimension(750,300));

        this.setBackground(java.awt.Color.WHITE);
        this.add(0,2,1,1, editConstraints, scroll);
        
        
        save = new JButton("테스트 실행 정보 저장");
        save.addActionListener((event)->{
            //
            String type = (String)combo.getSelectedItem();
            String connection = textArea.getText();
            Map<String,Object> m = gson.fromJson(connection, Map.class);
            if("KAFKA".equals(type)) m.put("type",type);
            Map<String,Object> params = new HashMap<>();
            params.put("run",m);
            params.put("jmx_file_name",TestAutomationGuiController.get_jmx_file_name());
            TestAutomationGuiController.save_run_config(params);
            
            JOptionPane.showMessageDialog(null, "테스트 데이터 실행 설정 정보가 정상적으로 저장 되었습니다.");
        });
        save.setBackground(new Color(0,133,252));
        save.setForeground(Color.WHITE);
        save.setBorderPainted(false);
        this.add(this,0,3,1,1, GridBagConstraints.EAST,GridBagConstraints.NONE, editConstraints, save);
        
        this.attach();
    }
    
    public String getConnectionText(){
        return this.textArea.getText();
    }
    
    public void setConnectionText(String txt){
        this.textArea.setText(txt);
    }
    
    public void attach(){
        if(testDataConfigPanel != null){
            testDataConfigPanel.getSampler().setExecutor(this.attachFdsPluginTestExecutor(this.getConnectionText()));
            testDataConfigPanel.getSampler().getExecutor().setTestData(this.attachFdsPluginTestData(this.toString()));
            // this.getFdsPluginPanel().getSampler().getExecutor().init(getFdsPluginPanel().getSampler().getExecutor().getTestData(),callback);
        }
    }    
    
    public TestPluginTestData attachFdsPluginTestData(String samplerKey) {
        TestPluginTestData testData = new TestPluginTestDataQueueImpl(samplerKey);
        testData.setConnectionInfo(this.getConnectionText());
        return testData;
    }    
    
    public TestPluginExecutor attachFdsPluginTestExecutor(String json) {
        try {
            logger.info("Executor's config map string : {}", json);
            if(json != null && json.length() != 0) {
                if (json.contains("\"url\"")) {
                    AbstractPluginExecutor http = ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.HTTP);
                    http.setConfigMap(gson.fromJson(json, Map.class));
                    return http;
                }
                else {
                    AbstractPluginExecutor kafka = ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.KAFKA);
                    kafka.setConfigMap(gson.fromJson(json, Map.class));
                    return kafka;
                }
            }else{
                AbstractPluginExecutor kafka = ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.KAFKA);
                kafka.setConfigMap(gson.fromJson(getDefaultText(), Map.class));
                return kafka;
            }
        }catch(Exception e){
            logger.error(e.toString());
            return ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.KAFKA);
        }
        
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

}
