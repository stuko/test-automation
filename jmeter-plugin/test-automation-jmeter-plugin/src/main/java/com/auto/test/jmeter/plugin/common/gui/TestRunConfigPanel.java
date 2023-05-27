/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.auto.test.jmeter.plugin.common.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.run.executor.TestPluginExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.async.AsyncExecutorManager;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;
import com.google.gson.Gson;
import com.netflix.jmeter.utils.SystemUtils;

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
   String[] method = {"DEFAULT","KAFKA","REST"};
   
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
       combo.addItemListener(e -> {
           if(e.getID() == ItemEvent.ITEM_STATE_CHANGED){
               if(e.getStateChange() == ItemEvent.SELECTED){
                   JComboBox<String> cb = (JComboBox<String>) e.getSource();
                   String newSelection = (String) cb.getSelectedItem();
                   logger.info("Selected item is {}", newSelection);
                   if(newSelection.equals("KAFKA")){
                       textArea.setText(TestAutomationGuiController.getKafkaText());
                   }else if(newSelection.equals("REST")){
                       textArea.setText(TestAutomationGuiController.getRestText());
                   }else if(newSelection.equals("TCP")){
                       textArea.setText(TestAutomationGuiController.getTcpText());
                   }else{
                       textArea.setText(TestAutomationGuiController.getDefaultText());
                   }
               }
           }
       });
       this.add(this,0,1,1,1, GridBagConstraints.WEST,GridBagConstraints.NONE, editConstraints, combo);
       
       textArea = new JTextArea();  //JTextArea 생성
       textArea.setRows(5);
       textArea.setBounds(50, 50, 300, 300); //JTeatArea 크기 및 위치 지정
       textArea.setEditable(true); //실행시 JtextArea edit 금지 (글을 쓸 수 없음) true면 가능
       textArea.setText(TestAutomationGuiController.getDefaultText());

       javax.swing.JScrollPane scroll = new JScrollPane(textArea);
       scroll.setPreferredSize(new java.awt.Dimension(750,300));

       this.setBackground(java.awt.Color.WHITE);
       this.add(0,2,1,1, editConstraints, scroll);
       
       
       save = new JButton("테스트 실행 정보 저장");
       save.addActionListener((event)->{
           //
           String type = (String)combo.getSelectedItem();
           String connection = textArea.getText();
           Map<String,Object> m = new HashMap<>();
           if(connection != null && connection.trim().length() > 0) {
        	   m = gson.fromJson(connection, Map.class);
        	   //if("KAFKA".equals(type)) m.put("type",type);
           }
           m.put("type",type);
           Map<String,Object> params = new HashMap<>();
           params.put("run",m);
           params.put("jmx_file_name",TestAutomationGuiController.get_jmx_file_name());
           
           try {
	   			AsyncExecutorManager.getINSTANCE().executeThread(()->{
	   				boolean stop = false;
	   				while(!stop) {
	   					try {
	   						TestAutomationGuiController.save_run_config(params);
	   						JOptionPane.showMessageDialog(null, "테스트 데이터 실행 설정 정보가 정상적으로 저장 되었습니다.");
	   						stop = true;
	   					}catch(Exception e) {
	   						// logger.info("Can not connect to Test Automation Server[TestAutomationGuiController.save_run_config(params)].. So, wait 30 seconds and Retry...." + e.toString());
	   						try {
	   							Thread.sleep(30000);
	   						} catch (InterruptedException e1) {
	   							logger.error(e1.toString());
	   						}
	   					}			
	   				}
	   			});
	   		} catch (Exception e) {
	   			logger.debug(SystemUtils.getStackTrace(e));
	   		}
           
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

           TestPluginExecutor testPluginExecutor = null;
           TestPluginTestData testPluginTestData = null;

           if(testDataConfigPanel.getSampler().getExecutor() != null && testDataConfigPanel.getSampler().getExecutor().getTYPE().name().equals((String)combo.getSelectedItem())){
               testPluginExecutor = testDataConfigPanel.getSampler().getExecutor();
           }else{
               testPluginExecutor = TestAutomationGuiController.get_test_executor((String)combo.getSelectedItem(), this.getConnectionText(), testDataConfigPanel.getPluginData());
           }

           if(testDataConfigPanel.getSampler().getExecutor() != null && testDataConfigPanel.getSampler().getExecutor().getTestData() != null){
               testPluginTestData = testDataConfigPanel.getSampler().getExecutor().getTestData();
           }else{
               testPluginTestData = TestAutomationGuiController.get_test_data(this.toString(), this.getConnectionText());
           }

           testDataConfigPanel.getSampler().setExecutor(testPluginExecutor);
           testDataConfigPanel.getSampler().getExecutor().setTestData(testPluginTestData);
           // this.getFdsPluginPanel().getSampler().getExecutor().init(getFdsPluginPanel().getSampler().getExecutor().getTestData(),callback);
       }
   }    
}