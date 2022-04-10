package com.kcb.fds.jmeter.plugin.common.gui;

import com.google.gson.Gson;
import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListQueue;
import com.kcb.fds.jmeter.plugin.common.sampler.TestPluginSampler;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginConstants;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginUtil;
import com.kcb.fds.plugin.test.result.FdsPluginTestResultForm;
import com.kcb.fds.jmeter.plugin.common.function.TestPluginCallBack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestDataConfigPanel extends PluginGridPanel {

    static Logger logger = LoggerFactory.getLogger(TestDataConfigPanel.class);

    DefaultTableModel defaultTableModel;
    JTable jTable;
    JScrollPane jScrollPane;
    JLabel header;
    JLabel jlabel;
    private int currentRow = 1;
    private TestPluginSampler sampler = null;
    String OK = "테스트 데이터 생성 하기";
    String NO = "테스트 데이터 생성 중지";
    AtomicBoolean is_start = new AtomicBoolean(false);
    JButton ok;
    JLabel status = new JLabel();
    Gson gson = new Gson();
    
    public TestDataConfigPanel(String title){
        initComponent(title,null);
    }

    public void initComponent(String title, TestPluginCallBack callback){
        display(title,callback);
        sampler = new TestPluginSampler();
    }

    private void display(String title , TestPluginCallBack callback) {
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setLayout(new GridBagLayout());
        GridBagConstraints labelConstraints = this.getLabelGBC();
        GridBagConstraints editConstraints = this.getEditGBC();
        labelConstraints.insets = new Insets(10, 10, 2, 4);
        editConstraints.insets = new Insets(10, 10, 2, 4);

        JLabel header =  new JLabel(title, JLabel.LEFT);
        header.setFont(new java.awt.Font("맑은 고딕", 1, 18));
        this.add(0,0,10,1, GridBagConstraints.NORTH, editConstraints, header);
                
        this.setBackground(Color.WHITE);
        defaultTableModel = new DefaultTableModel(new String[] { "항목 이름(영문)", "유형", "값" , "갯수", "길이", "암호화" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        jlabel = new JLabel();

        jTable = new JTable(defaultTableModel);
        // jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // jTable.getColumnModel().getColumn(0).setPreferredWidth(27);
        setColumnWidths(jTable, 100,100,450,50,50);

        jScrollPane = new JScrollPane(jTable);
        jScrollPane.setMinimumSize(new Dimension(400,300));
        JButton open = new JButton("테스트 데이터 정보 불러오기");
        open.addActionListener((event)->{
            open();
        });
        open.setBackground(new Color(0,133,252));
        open.setForeground(Color.WHITE);
        open.setBorderPainted(false);
        JButton add = new JButton("+");
        add.addActionListener((event)->{
            add();
        });
        add.setBackground(new Color(0,133,252));
        add.setForeground(Color.WHITE);
        add.setBorderPainted(false);
        JButton save = new JButton("테스트 데이터 설정 저장");
        save.addActionListener((event)->{
            
            if(TestAutomationGuiController.get_jmx_file_name() == null || "".equals(TestAutomationGuiController.get_jmx_file_name())){
                JOptionPane.showMessageDialog(null, "JMeter 파일을 저장하신 후, 저장 버튼을 클릭해 주세요.");
                return;
            }
            
            String[][] test_data = this.getPluginData();
            this.getSampler().getExecutor().getTestData().setData(test_data);
            List<Map<String,Object>> test_data_list_map = new ArrayList<>();
            for(String[] data : test_data){
                // "항목 이름(영문)", "유형", "값" , "갯수", "길이", "암호화"
                Map<String,Object> m = new HashMap<>();
                m.put("name", data[0]);
                m.put("type", data[1]);
                m.put("value", data[2]);
                m.put("count", data[3]);
                m.put("length", data[4]);
                m.put("encode", data[5]);
                test_data_list_map.add(m);
            }
            Map<String,Object> params = new HashMap<>();
            params.put("factors",test_data_list_map);
            params.put("jmx_file_name",TestAutomationGuiController.get_jmx_file_name());
            
            TestAutomationGuiController.save_factors(params);
            
            JOptionPane.showMessageDialog(null, "테스트 데이터 설정 정보가 정상적으로 저장 되었습니다.");
        });
        save.setBackground(new Color(0,133,252));
        save.setForeground(Color.WHITE);
        save.setBorderPainted(false);
        JButton remove = new JButton("-");
        remove.addActionListener((event)->{
            if(jTable.getSelectedRow() != -1) {
                // remove selected row from the model
                java.util.List<String> delColumns = new ArrayList<>();
                for(int r: jTable.getSelectedRows()){
                    delColumns.add(defaultTableModel.getValueAt(r,0) == null  || "".equals(defaultTableModel.getValueAt(r,0).toString()) ? "" : defaultTableModel.getValueAt(r,0).toString());
                }
                for(String colName : delColumns){
                    deleteRow(colName);
                }
                JOptionPane.showMessageDialog(null, "선택하신 라인의 데이터가 삭제 되었습니다.");
            }
        });
        remove.setBackground(new Color(0,133,252));
        remove.setForeground(Color.WHITE);
        remove.setBorderPainted(false);
        JButton report = new JButton("REPORT");
        report.addActionListener((event)->{
        	FdsPluginTestResultForm fds = new FdsPluginTestResultForm();
            fds.setVisible(true);
        });

        status.setText("테스트 데이터 건수 : 0");
        status.setPreferredSize(new java.awt.Dimension(100,40));
        ok = new JButton(OK);
        ok.setBackground(new Color(0,133,252));
        ok.setForeground(Color.WHITE);
        ok.setBorderPainted(false);
        ok.addActionListener(event->{
            if(!is_start.get()) {
                com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListQueue.getInstance(TestPluginConstants.fds_data_path).removeAll();
                this.getSampler().getExecutor().start();
                is_start.set(true);
                this.getSampler().getExecutor().getTestData().setData(this.getPluginData());
                if(callback == null){
                    this.getSampler().getExecutor().init(this.getSampler().getExecutor().getTestData(), (d,cnt)->{
                        // logger.info(d);
                        FileJsonArrayListQueue.getInstance(TestPluginConstants.fds_data_path).write(d);
                        setTestCount(cnt);
                        return null;
                    });
                }else{
                    this.getSampler().getExecutor().init(this.getSampler().getExecutor().getTestData(), callback);
                }
                
                ok.setText(NO);
            }else{
                this.getSampler().getExecutor().stop();
                is_start.set(false);
                ok.setText(OK);
            }
        }); 
        
        
       JPanel b_panel = new JPanel();
       b_panel.setLayout(new GridBagLayout());
       b_panel.setBackground(Color.WHITE);
       this.add(b_panel,0,0,1,1,GridBagConstraints.EAST,GridBagConstraints.NONE, editConstraints,jlabel);
       this.add(b_panel,1,0,1,1,GridBagConstraints.EAST,GridBagConstraints.NONE, editConstraints,status);
       this.add(b_panel,2,0,1,1,GridBagConstraints.EAST,GridBagConstraints.NONE, editConstraints,add);
       this.add(b_panel,3,0,1,1,GridBagConstraints.EAST,GridBagConstraints.NONE, editConstraints,remove);
        // b_panel.add(open);
        // b_panel.add(add);
        // SAVE 기능이 불필요하여, Disabled 됨.
        // b_panel.add(save);
        // b_panel.add(remove);
        // b_panel.add(report);
        
        JPanel blank = new JPanel();
        blank.setPreferredSize(
                new Dimension(550,
                        50));
        blank.setBackground(Color.WHITE);
        this.add(0,1,9, 1, editConstraints, blank);
        this.add(9,1,1, 1, GridBagConstraints.EAST,editConstraints, b_panel);
        // this.add(2,1,1, 1, labelConstraints, remove);
        this.add(0,2,10, 1, GridBagConstraints.CENTER, editConstraints, jScrollPane);
        
        /*
        this.add(this, 0,3,7,1,GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, blank);
        this.add(this, 7,3,1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, ok);
        this.add(this, 8,3,1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, open);
        this.add(this, 9,3,1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, save);
        */
        
        this.add(this,0,3,1,1, GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, ok);
        this.add(this,1,3,1,1, GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, open);
        this.add(this,2,3,1,1, GridBagConstraints.EAST, GridBagConstraints.NONE, editConstraints, save);
        
        addTestData(new File(new File(TestPluginConstants.fds_test_define_path),TestPluginConstants.fds_test_define_file));

        // this.setPreferredSize(new Dimension(750,800));
        // jScrollPane.setPreferredSize(new Dimension(750, 400));

    }

    private void open(){
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            if(file.exists()) {
                addTestData(file);
            }else{
                jlabel.setText("파일이 존재 하지 않습니다 : " + file.getName());
            }

        }
    }

    private void deleteRow(String colName) {
        try {
            int till = defaultTableModel.getRowCount();
            for (int i = 0; i < till; i++) {
                if (colName.equals(defaultTableModel.getValueAt(i, 0))) {
                    defaultTableModel.removeRow(i);
                    deleteRow(colName);
                    break;
                }
            }
        }catch(Exception e){
            logger.error(e.toString());
        }
    }

    public void loadTestData(List factors){
        Vector vector = this.defaultTableModel.getDataVector();
        try{
            if(factors == null || factors.size() == 0) return;
            this.clear();
            for(Object object : factors){
                Map<String,Object> factor = (Map<String,Object>)object;
                String[] row = new String[6];
                row[0] = (String)factor.get("name");
                row[1] = (String)factor.get("type");
                row[2] = (String)factor.get("value");
                row[3] = (String)factor.get("count");
                row[4] = (String)factor.get("length");
                row[5] = (String)factor.get("encode");
                this.defaultTableModel.addRow(row);
            }
        }catch(Exception e){
            try{
                this.clear();
                if(vector != null)this.defaultTableModel.addRow(vector);
            }catch(Exception ee){logger.error(ee.toString(), ee);}
        }
    }
    
    public void addTestData(File f){

        clear();

        String[] row1 = {"name","string","john,mike,selly|tod,mac,april|jorge,hanse,mom","10","10","N"};
        String[] row2 = {"age","number","20~30|30~40|40~50|60~70|1~10","6","10","N"};
        String[] row3 = {"gender","string","F,M","10","10","N"};
        // 1형식:  20210301~20210405,yyyymmdd
        // 2형식: -20,day,yyyymmdd
        String[] row4 = {"applyDate","datetime","20210301000000~20210331000000,yyyymmddhhmmss|20200301000000~20200331000000,yyyymmddhhmmss","10","10","N"};
        String[] row5 = {"cancelDate","datetime","-10,day,yyyymmddhhmmss|-20,day,yyyymmddhhmmss","10","10","N"};
        String[] row6 = {"id","key","X,40,Y|prefix,6,postfix","10","10","N"};

        if(!f.exists()){
            logger.error("File {} does not exist", f.getAbsolutePath());
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(Paths.get(f.getAbsolutePath()), Charset.forName(TestPluginUtil.findFileEncoding(f)))){
            String line;
            while ((line = br.readLine()) != null) {
                this.defaultTableModel.addRow(line.split("\t"));
            }
            jlabel.setText("파일명 : " + f.getName());
        } catch (Exception e) {
            logger.error("File {} does not exist", f.getAbsolutePath());
            logger.error(e.toString(),e);
        }
    }

    public void clear(){
        this.defaultTableModel.getDataVector().removeAllElements();
        this.defaultTableModel.fireTableDataChanged();
        repaint();
    }

    public void add(){
        String[] row = new String[this.defaultTableModel.getColumnCount()];
        this.defaultTableModel.addRow(row);
    }

    public static void setColumnWidths(JTable table, int... widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length; i++) {
            if (i < columnModel.getColumnCount()) {
                columnModel.getColumn(i).setMinWidth(widths[i]);
            }
            else break;
        }
    }

    public void add(String[] row){
        this.defaultTableModel.addRow(row);
    }

    public String[][] getPluginData(){
        logger.info("read plugin data");
        if(this.defaultTableModel == null || this.defaultTableModel.getRowCount() == 0){
            logger.info("read plugin data's data model is NULL");
            return null;
        }
        String[][] data = new String[this.defaultTableModel.getColumnCount()][this.defaultTableModel.getRowCount()];
        // this.defaultTableModel.getDataVector().toArray(data);
        logger.info("create plugin data");
        data = getTableData(this.jTable);
        return data;
    }

    public String[][] getTableData (JTable table) {
        TableModel dtm = table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        String[][] tableData = new String[nRow][nCol];
        for (int i = 0 ; i < nRow ; i++)
            for (int j = 0 ; j < nCol ; j++)
                tableData[i][j] = String.valueOf(dtm.getValueAt(i,j));
        return tableData;
    }

    public int getRow() {
        return currentRow++;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public TestPluginSampler getSampler() {
        return sampler;
    }

    public void setSampler(TestPluginSampler sampler) {
        this.sampler = sampler;
    }
    
    public void setTestCount(long cnt){
         this.status.setText(cnt + " 건");
    }    
}
