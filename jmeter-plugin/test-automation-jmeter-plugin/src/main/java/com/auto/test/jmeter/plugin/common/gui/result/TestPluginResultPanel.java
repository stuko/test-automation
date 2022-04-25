package com.auto.test.jmeter.plugin.common.gui.result;

import com.auto.test.jmeter.plugin.common.gui.PluginGridPanel;
import com.auto.test.jmeter.plugin.common.gui.table.TestPluginTablePanel;

import javax.swing.*;

public class TestPluginResultPanel extends PluginGridPanel {

	// DataBase or Kafka
    // kafka : broker_url , topic_name
    // database : url , class_name, id, pw , query
    // file : file path
    JLabel result_label = new JLabel("Test Result Data Set");
    JTextArea result_config = new JTextArea();
    JTextArea result_query = new JTextArea();
    TestPluginTablePanel result_table_panel;


    // DataBase or Kafka
    // kafka : broker_url , topic_name
    // database : url , class_name, id, pw , query
    // file : file path
    JLabel expect_label = new JLabel("Test Expected Data Set");
    JTextArea expect_config = new JTextArea();
    JTextArea expect_query = new JTextArea();
    TestPluginTablePanel expect_table_panel;

    // 항목 이름들을 나열한 정보
    JTextField column_names = new JTextField();
    JLabel column_label = new JLabel("항목명");
    JPanel column_panel = new JPanel();


    public TestPluginResultPanel(String ... names){
        column_panel.add(column_label);
        column_panel.add(column_names);
        this.add(0,0,1,1,this.getEditGBC(),column_panel);
    }




}
