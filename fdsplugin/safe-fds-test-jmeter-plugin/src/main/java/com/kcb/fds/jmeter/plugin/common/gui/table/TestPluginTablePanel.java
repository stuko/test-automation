package com.kcb.fds.jmeter.plugin.common.gui.table;

import com.kcb.fds.jmeter.plugin.common.gui.PluginGridPanel;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginUtil;
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

public class TestPluginTablePanel extends PluginGridPanel {

    static Logger logger = LoggerFactory.getLogger(TestPluginTablePanel.class);

    DefaultTableModel defaultTableModel;
    JTable jTable;
    JScrollPane jScrollPane;
    JLabel jLabel = new JLabel("");
    int MAX_ROW = 100;
    String[] columnNames;

    public TestPluginTablePanel(String ... names) {
        defaultTableModel = new DefaultTableModel(names, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        jTable = new JTable(defaultTableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // setColumnWidths(jTable, 100, 100, 450, 50, 50);
        jScrollPane = new JScrollPane(jTable);
        JPanel b_panel = new JPanel();

        JButton open = new JButton("OPEN");
        open.addActionListener((event) -> {
            open();
        });

        JButton button = new JButton("ADD");
        button.addActionListener((event) -> {
            add();
        });

        JButton remove = new JButton("REMOVE");
        remove.addActionListener((event) -> {
            if (jTable.getSelectedRow() != -1) {
                // remove selected row from the model
                java.util.List<String> delColumns = new ArrayList<>();
                for (int r : jTable.getSelectedRows()) {
                    delColumns.add(defaultTableModel.getValueAt(r, 0) == null || "".equals(defaultTableModel.getValueAt(r, 0).toString()) ? "" : defaultTableModel.getValueAt(r, 0).toString());
                }
                for (String colName : delColumns) {
                    deleteRow(colName);
                }
                JOptionPane.showMessageDialog(null, "Selected row deleted successfully");
            }
        });

        b_panel.add(jLabel);
        b_panel.add(open);
        b_panel.add(button);
        b_panel.add(remove);
        this.setColumnNames(names);
        this.setLayout(new GridBagLayout());
        GridBagConstraints labelConstraints = this.getLabelGBC();
        GridBagConstraints editConstraints = this.getEditGBC();
        this.add(0, 0, 4, 1, labelConstraints, b_panel);
        this.add(0, 1, 4, 1, editConstraints, jScrollPane);
    }

    public void reloadTableColumnName(String[] names){
        this.defaultTableModel = new DefaultTableModel(names, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        jTable = new JTable(defaultTableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // setColumnWidths(jTable, 100, 100, 450, 50, 50);
        jScrollPane = new JScrollPane(jTable);
    }

    public void setMaxRow(int row){
        this.MAX_ROW = row;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
        this.reloadTableColumnName(this.columnNames);
    }

    private void open(){
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            if(file.exists()) {
                addTableData(file);
            }else{
                jLabel.setText("File does not exist : " + file.getName());
            }

        }
    }

    public static void setColumnWidths(JTable table, int... widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length; i++) {
            if (i < columnModel.getColumnCount()) {
                columnModel.getColumn(i).setMaxWidth(widths[i]);
            }
            else break;
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

    public void addTableData(File f){
        clear();
        if(!f.exists()){
            logger.error("File {} does not exist", f.getAbsolutePath());
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(Paths.get(f.getAbsolutePath()), Charset.forName(TestPluginUtil.findFileEncoding(f)))){
            String line;
            while ((line = br.readLine()) != null) {
                if(line.indexOf("\t") > 0)
                    add(line.split("\t"));
                else if(line.indexOf(",") > 0)
                    add(line.split(","));
                else add(line.split(","));
            }
            jLabel.setText("File Selected: " + f.getName());
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

    public void add(String[] row){
        if(this.defaultTableModel.getRowCount() <= MAX_ROW)
            this.defaultTableModel.addRow(row);
    }

    public String[][] getTableData(){
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

}
