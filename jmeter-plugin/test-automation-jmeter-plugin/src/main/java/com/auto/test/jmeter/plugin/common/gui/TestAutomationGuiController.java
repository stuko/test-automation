/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.auto.test.jmeter.plugin.common.gui;

import com.auto.test.jmeter.plugin.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *
 * @author O218001_D
 */
public class TestAutomationGuiController {
    
    static Logger logger = LoggerFactory.getLogger(TestAutomationGuiController.class);
    static String KANBOARD_URL = "http://localhost:5000/";
    static Gson gson = new Gson();
    static List<Map> project_list;
    
    public static void show_project_list(JList list
            , TestAutomationMainGui mainGui
            , TestDataConfigPanel testDataConfigPanel
            , TestRunConfigPanel testRunConfigPanel){
        try{
            if(list != null){
                HttpUtil.call(KANBOARD_URL+"get_project_list","",(body)->{
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
                HttpUtil.call(KANBOARD_URL+"get_project_detail",gson.toJson(param),(body)->{
                   Map<String,String> project_detail = gson.fromJson(body, Map.class);
                   jenkins_url.setText(project_detail.get("jenkins_server_url"));
                   jenkins_project_name.setText(project_detail.get("jenkins_project_name"));
                   jenkins_token.setText(project_detail.get("jenkins_token"));
                   mattermost_webhook_id.setText(project_detail.get("mattermost_webhook_id"));
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
            String jmx_file_name = get_jmx_file_name();
            if(jmx_file_name == null || "".equals(jmx_file_name)) return false;
            
            project.put("jmx_file_name", jmx_file_name);
            logger.info("parameter is  {}",gson.toJson(project));
            HttpUtil.call(KANBOARD_URL+"get_project_detail_by_jmx",gson.toJson(project),(body)->{
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
                File f = new File(TestAutomationGuiController.get_jmx_file_name());
                
                param.put("project_id", data);
                param.put("jmx_file_name", f.getName());
                param.put("jenkins_server_url", jenkins_server_url);
                param.put("jenkins_project_name", jenkins_project_name);
                param.put("jenkins_token", jenkins_token);
                param.put("mattermost_webhook_id", mattermost_webhook_id);
                HttpUtil.call(KANBOARD_URL+"save_project_info",gson.toJson(param),(body)->{
                    logger.info("jmx_file_name is {}", TestAutomationGuiController.get_jmx_file_name());
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
        HttpUtil.call(KANBOARD_URL+"save_factors",gson.toJson(param),(body)->{
               logger.info(body);
        });
    }
    
    public static void save_run_config(Map<String,Object> param){
        HttpUtil.call(KANBOARD_URL+"save_run_config",gson.toJson(param),(body)->{
               logger.info(body);
        });
    }
     
    public static String get_jmx_file_name(){
        return org.apache.jmeter.gui.GuiPackage.getInstance().getTestPlanFile();
    }

    public static void save_test_scenario(){
        try{
            logger.info("save url is : {}",KANBOARD_URL+"upload" );
            HttpUtil.uploadFileFromOkhttp(KANBOARD_URL+"upload", get_jmx_file_name());
        }catch(Exception e){
            logger.error(e.toString(), e);
        }finally{
        }
    }

}
