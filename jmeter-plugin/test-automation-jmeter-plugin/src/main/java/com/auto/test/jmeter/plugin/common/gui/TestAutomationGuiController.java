/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.auto.test.jmeter.plugin.common.gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.auto.test.jmeter.plugin.common.server.ShellServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestData;
import com.auto.test.jmeter.plugin.common.data.TestPluginTestDataQueueImpl;
import com.auto.test.jmeter.plugin.common.run.executor.AbstractPluginExecutor;
import com.auto.test.jmeter.plugin.common.run.executor.ExecutorMap;
import com.auto.test.jmeter.plugin.common.run.executor.TestPluginExecutor;
import com.auto.test.jmeter.plugin.common.util.HttpUtil;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import com.google.gson.Gson;

/**
 *
 * @author O218001_D
 */
public class TestAutomationGuiController {
    
    static Logger logger = LoggerFactory.getLogger(TestAutomationGuiController.class);
    static String TEST_URL = "http://localhost:5000/";
    static String SHELL_PORT = "9999";
    static Gson gson = new Gson();
    static List<Map> project_list;
    
    static {
    	if(System.getProperty("TEST_URL") != null)TEST_URL=System.getProperty("TEST_URL");
        if(System.getProperty("SHELL_PORT") != null)SHELL_PORT=System.getProperty("SHELL_PORT");
    }
    
    public static TestPluginTestData get_test_data(String samplerKey, String connectionText) {
    	TestPluginTestData testData = new TestPluginTestDataQueueImpl(samplerKey);
        testData.setConnectionInfo(connectionText);
        return testData;
    }
    
    public static TestPluginExecutor get_test_executor(String type, String json) {
    	try {
    		logger.info("Executor's type is : {}", type);
    		logger.info("Executor's config map string : {}", json);
            if(type != null && !type.equals("DEFAULT") && json != null && json.length() != 0) {
                if (json.contains("\"url\"")) {
                    AbstractPluginExecutor http = ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.HTTP);
                    http.setConfigMap(gson.fromJson(json, Map.class));
                    return http;
                }else {
                    AbstractPluginExecutor kafka = ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.KAFKA);
                    kafka.setConfigMap(gson.fromJson(json, Map.class));
                    return kafka;
                }
            }else{
                AbstractPluginExecutor default_executor = ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.DEFAULT);
                default_executor.setConfigMap(gson.fromJson(getDefaultText(), Map.class));
                // TEST
                default_executor.start();
                default_executor.setTestData(get_test_data("DEFAULT",null));
                get_test_data_by_jmx(list->{
                	String[][] test_data_factors = new String[list.size()][];
                	for(int i = 0; i < list.size() ; i++) {
                	    Map<String,Object> factor = (Map<String,Object>)list.get(i);
                        String[] row = new String[factor.size()];
                        
                        row[0] = (String)factor.get("name");
                        row[1] = (String)factor.get("type");
                        row[2] = (String)factor.get("value");
                        row[3] = (String)factor.get("count");
                        row[4] = (String)factor.get("length");
                        row[5] = (String)factor.get("encode");
                        test_data_factors[i] = row;
                	}
                	default_executor.getTestData().setData(test_data_factors);
                	default_executor.init(default_executor.getTestData(), (d,cnt)->{
                		FileJsonArrayListQueue.getInstance(TestPluginConstants.ta_data_path).write(d);
                        return null;
                	});
                });
                ShellServer.getInstance().start(Integer.parseInt(SHELL_PORT));

                return default_executor;
            }
        }catch(Exception e){
            logger.error(e.toString());
            return ExecutorMap.getInstance().getExecutor(ExecutorMap.ExecutorType.DEFAULT);
        }
    }
    
    public static String getDefaultText() {
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
    
    public static void show_project_list(JList list
            , TestAutomationMainGui mainGui
            , TestDataConfigPanel testDataConfigPanel
            , TestRunConfigPanel testRunConfigPanel){
        try{
            if(list != null){
                HttpUtil.call(TEST_URL+"get_project_list","",(body)->{
                   project_list = gson.fromJson(body, List.class);
                   String[] list_data = new String[project_list.size()];
                   int i = 0;
                   for(Map project : project_list){
                       list_data[i++] = (String)project.get("id") + "," + (String)project.get("name");
                   }
                   list.setListData(list_data);
                });
            }
        }catch(Exception e){
            logger.error(e.toString(), e);
        }
    }
    
    public static boolean show_project_detail(JList list,JTextField project_name,JTextField jenkins_url,JTextField jenkins_project_name,JTextField jenkins_token,JTextField mattermost_webhook_id,JTextArea project_desc){
        try{
            if(list != null){
                Map<String,String> param = new HashMap<>();
                if(list.getSelectedValue() == null){
                    return false;
                }
                String selected_data = (String)list.getSelectedValue();
                String data = selected_data.split(",")[0];
                Map<String,String> project = project_list.stream().filter(x-> data.equals(x.get("id"))).findAny().get();
                project_name.setText(project.get("name"));
                project_desc.setText(project.get("description"));
                param.put("project_id", data);
                HttpUtil.call(TEST_URL+"get_project_detail",gson.toJson(param),(body)->{
                   Map<String,String> project_detail = gson.fromJson(body, Map.class);
                   if (project_detail != null) {
                       jenkins_url.setText(project_detail.get("jenkins_server_url"));
                       jenkins_project_name.setText(project_detail.get("jenkins_project_name"));
                       jenkins_token.setText(project_detail.get("jenkins_token"));
                       mattermost_webhook_id.setText(project_detail.get("mattermost_webhook_id"));
                   }
                });
                
            }
            return true;
        }catch(Exception e){
            logger.error(e.toString(), e);
            return false;
        }
        
    }
    
    public static boolean show_project_detail_by_jmx(
              TestAutomationMainGui mainGui
            , TestDataConfigPanel testDataConfigPanel
            , TestRunConfigPanel testRunConfigPanel
            ){
        try{
            Map<String,String> project = new HashMap<>();
            // project.put("jmx_file_name", get_jmx_file_name().replaceAll("\\","/"));
            if(get_jmx_file_name() == null) return false;
            String jmx_file_name = get_jmx_file_name().getName();
            if(jmx_file_name == null || "".equals(jmx_file_name)) return false;
            
            project.put("jmx_file_name", jmx_file_name);
            logger.info("parameter is  {}",gson.toJson(project));
            HttpUtil.call(TEST_URL+"get_project_detail_by_jmx",gson.toJson(project),(body)->{
                logger.info(body);
                List list = gson.fromJson(body, List.class);
                if(list != null && list.size() > 0) {
                    Map<String,Object> m = (Map<String,Object>)list.get(0);
                    String project_id = (String)m.get("project_id");
                    if(project_list != null && project_list.size() > 0){
                       Map<String,String> pm = project_list.stream().filter(x-> project_id.equals(x.get("id"))).findAny().get();
                       mainGui.selectProject(project_id);
                       mainGui.setProjectName(pm.get("name"));
                       mainGui.setProjectDetail(pm.get("description"));
                    }
                    // String jmx_file_name = (String)m.get("jmx_file_name");
                    mainGui.setJenkinsServerUrl((String)m.get("jenkins_server_url"));
                    mainGui.setJenkinsProjectName((String)m.get("jenkins_project_name"));
                    mainGui.setJenkinsToken((String)m.get("jenkins_token"));
                    mainGui.setMattermostWebHookId((String)m.get("mattermost_webhook_id"));
                    List factors = (List)m.get("factors");
                    testDataConfigPanel.loadTestData(factors);
                    Map<String,Object> runConfig = (Map<String,Object>)m.get("run");
                    testRunConfigPanel.loadTestRun(runConfig);
                }
             });
            return true;
        }catch(Exception e){
            logger.error(e.toString(), e);
            return false;
        }
    }
    
    public static boolean get_test_data_by_jmx(TestDataFactorReceiver receiver){
      try{
          Map<String,String> project = new HashMap<>();
          if(get_jmx_file_name() == null) return false;
          String jmx_file_name = get_jmx_file_name().getName();
          if(jmx_file_name == null || "".equals(jmx_file_name)) return false;
          
          project.put("jmx_file_name", jmx_file_name);
          logger.info("parameter is  {}",gson.toJson(project));
          HttpUtil.call(TEST_URL+"get_project_detail_by_jmx",gson.toJson(project),(body)->{
              logger.info(body);
              List list = gson.fromJson(body, List.class);
              if(list != null && list.size() > 0) {
                  Map<String,Object> m = (Map<String,Object>)list.get(0);
                  String project_id = (String)m.get("project_id");
                  if(project_list != null && project_list.size() > 0){
                     Map<String,String> pm = project_list.stream().filter(x-> project_id.equals(x.get("id"))).findAny().get();
                  }
                  List factors = (List)m.get("factors");
                  if(factors != null && factors.size() > 0) receiver.receive_factor(factors);
              }
           });
          return true;
      }catch(Exception e){
          logger.error(e.toString(), e);
          return false;
      }
  }
    
    public static boolean save_project_connection_info(javax.swing.JList list , String jmx_file_name, String jenkins_server_url, String jenkins_project_name, String jenkins_token, String mattermost_webhook_id){
         try{
            if(list != null){
                Map<String,String> param = new HashMap<>();
                String selected_data = (String)list.getSelectedValue();
                if(list.getSelectedValue() == null){
                    return false;
                }
                if(jmx_file_name == null){
                    return false;
                }
                if(jenkins_server_url == null){
                    return false;
                }
                if(jenkins_token == null){
                    return false;
                }
                if(mattermost_webhook_id == null){
                    return false;
                }
                String data = selected_data.split(",")[0];
                File f = TestAutomationGuiController.get_jmx_file_name();
                
                param.put("project_id", data);
                param.put("jmx_file_name", f.getName());
                param.put("jenkins_server_url", jenkins_server_url);
                param.put("jenkins_project_name", jenkins_project_name);
                param.put("jenkins_token", jenkins_token);
                param.put("mattermost_webhook_id", mattermost_webhook_id);
                HttpUtil.call(TEST_URL+"save_project_info",gson.toJson(param),(body)->{
                    logger.info("jmx_file_name is {}", TestAutomationGuiController.get_jmx_file_name().getName());
                   logger.info(body);
                });
            }
            return true;
        }catch(Exception e){
            logger.error(e.toString(), e);
            return false;
        }
        
    }
    
    public static void save_factors(Map<String,Object> param){
        HttpUtil.call(TEST_URL+"save_factors",gson.toJson(param),(body)->{
               logger.info(body);
        });
    }
    
    public static void save_run_config(Map<String,Object> param){
        HttpUtil.call(TEST_URL+"save_run_config",gson.toJson(param),(body)->{
               logger.info(body);
        });
    }
     
    public static File get_jmx_file_name(){
    	try {return new File(org.apache.jmeter.gui.GuiPackage.getInstance().getTestPlanFile());
    	}catch(Exception e) {return null;}
    }

    public static void save_test_scenario(){
        try{
            logger.info("save url is : {}",TEST_URL+"upload" );
            HttpUtil.uploadFileFromOkhttp(TEST_URL+"upload", get_jmx_file_name().getName());
        }catch(Exception e){
            logger.error(e.toString(), e);
        }finally{
        }
    }

}
