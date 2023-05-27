package com.auto.test.jmeter.plugin.database.cassandra;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;

import com.auto.test.jmeter.plugin.database.AbstractSummerizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraListenerGUI extends AbstractListenerGui implements CassandraQueryTimeListener {

	DefaultTableModel defaultTableModel;
	JTable jTable;
	JScrollPane jScrollPane;

	Logger logger = LoggerFactory.getLogger(CassandraListenerGUI.class);

	public CassandraListenerGUI() {
		super();
		init();
		logger.info("########## Constructed ...");
	}

	@Override
	public String getLabelResource() {
		return "Query 테스트 상세 리포트";
	}

	@Override
	public String getStaticLabel() {
		return "Query 테스트 상세 리포트";
	}

	@Override
	public void configure(TestElement el) {
		super.configure(el);
		this.defaultTableModel.fireTableDataChanged();
		logger.info("########## Configured ...");
	}

	@Override
	public TestElement createTestElement() {
		AbstractSummerizer summerizer = new CassandraSummary();
		modifyTestElement(summerizer);
		logger.info("########## Created ...");
		return summerizer;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		configure(element);
		this.defaultTableModel.fireTableDataChanged();
		logger.info("########## Modified ...");
	}

	@Override
	public void clearGui() {
		super.clearGui();
		// this.defaultTableModel.getDataVector().removeAllElements();
		this.defaultTableModel.fireTableDataChanged();
		repaint();
		if (this.defaultTableModel.getRowCount() <= 6) {
			for (int i = 0; i < 6 - this.defaultTableModel.getRowCount() ; i++) {
				this.defaultTableModel.addRow(new String[5]);
			}
		}
		logger.info("########## Cleard ...");
	}
	
	

	private void init() {

		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

		GridBagConstraints editConstraints = new GridBagConstraints();
		editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editConstraints.weightx = 1.0;
		editConstraints.fill = GridBagConstraints.HORIZONTAL;

		defaultTableModel = new DefaultTableModel(new String[] { "Query", "Max", "Min", "Avg", "Count" }, 6) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		CassandraInstantPluginMap.getInstance().setListener(this);

		// defaultTableModel.addRow(CassandraInstantPluginMap.getInstance().getQueryTime());
		jTable = new JTable(defaultTableModel);
		jScrollPane = new JScrollPane(jTable);

		init(mainPanel, labelConstraints, editConstraints);

		JPanel container = new JPanel(new BorderLayout());
		container.add(mainPanel, BorderLayout.NORTH);
		add(container, BorderLayout.CENTER);

	}

	public void init(JPanel mainPanel, GridBagConstraints labelConstraints, GridBagConstraints editConstraints) {
		addToPanel(mainPanel, labelConstraints, 0, 1, new JLabel("Query 통계 정보 ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 0, 2, jScrollPane);
	}

	public void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
		constraints.gridx = col;
		constraints.gridy = row;
		panel.add(component, constraints);
	}

	@Override
	public void applyQueryTime(String[][] data) {

		logger.info("1. applyQueryTime' table length is {} , data length is {}", this.defaultTableModel.getRowCount(), data.length);

		if (this.defaultTableModel.getRowCount() <= data.length) {
			String[] row = new String[data[0].length];
			for (int i = 0; i < data.length - this.defaultTableModel.getRowCount() ; i++) {
				this.defaultTableModel.addRow(row);
			}
		}

		logger.info("2. applyQueryTime' table length is {} , data length is {}", this.defaultTableModel.getRowCount(), data.length);

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				this.defaultTableModel.setValueAt(data[i][j], i, j);
			}
		}

		logger.info("3. applyQueryTime' table length is {} , data length is {}", this.defaultTableModel.getRowCount(), data.length);

		this.defaultTableModel.fireTableDataChanged();
	}
}
