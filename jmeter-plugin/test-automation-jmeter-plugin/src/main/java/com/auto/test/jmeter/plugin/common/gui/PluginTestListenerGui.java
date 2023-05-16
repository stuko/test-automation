package com.auto.test.jmeter.plugin.common.gui;

import com.auto.test.jmeter.plugin.common.listener.PluginTestListener;
import com.auto.test.jmeter.plugin.common.summerizer.TestPluginSummerizer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PluginTestListenerGui extends AbstractListenerGui {

    DefaultTableModel defaultTableModel;
    JTable jTable;
    JScrollPane jScrollPane;

    public PluginTestListenerGui(){
        super();
        init();
    }

   @Override
	public String getLabelResource() {
		return "Test Automation Result Listener :)";
	}

	@Override
	public String getStaticLabel() {
		return "Test Automation Result Listener :)";
	}
	
    @Override
    public TestElement createTestElement() {
        PluginTestListener listener = new PluginTestListener();
        modifyTestElement(listener);
        return listener;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
    }

    @Override
    public void clearGui(){
        super.clearGui();
        this.defaultTableModel.getDataVector().removeAllElements();
        this.defaultTableModel.fireTableDataChanged();
        repaint();
    }

    public void init(){
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        GridBagConstraints editConstraints = new GridBagConstraints();
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.weightx = 1.0;
        editConstraints.fill = GridBagConstraints.HORIZONTAL;

        defaultTableModel = new DefaultTableModel(new String[] { "Query", "Max", "Min", "Avg", "Count" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // defaultTableModel.addRow(CassandraInstantPluginMap.getInstance().getQueryTime());
        jTable = new JTable(defaultTableModel);
        jScrollPane = new JScrollPane(jTable);

        initPanel(mainPanel, labelConstraints, editConstraints);

        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }
    public void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
        constraints.gridx = col;
        constraints.gridy = row;
        panel.add(component, constraints);
    }
    public void initPanel(JPanel mainPanel, GridBagConstraints labelConstraints, GridBagConstraints editConstraints) {
        addToPanel(mainPanel, labelConstraints, 0, 1, new JLabel("Query Stats Report ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 0, 2, jScrollPane);
    }

    public void applyQueryTime(String[][] data) {
        if (this.defaultTableModel.getRowCount() <= data.length) {
            String[] row = new String[data[0].length];
            for (int i = 0; i < data.length; i++) {
                this.defaultTableModel.addRow(row);
            }
        }
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                this.defaultTableModel.setValueAt(data[i][j], i, j);
            }
        }

    }

}
