package com.auto.test.jmeter.plugin.database.postgres;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.database.cassandra.CassandraInstantPluginMap;
import com.netflix.jmeter.gui.AbstractGUI;

public class PostgresPluginGUI extends AbstractGUI {

	static Logger logger = LoggerFactory.getLogger(PostgresPluginGUI.class);
	
	private JTextField OC_NAME;
	private JTextField OC_URL;
	private JTextField OC_DRIVER;
	private JTextField OC_MIN;
	private JTextField OC_MAX;
	private JTextField OC_ID;
	private JTextField OC_PW;
	private JTextField OC_SVC_NAME;
	private JTextField OC_TEST_FILE_PATH;
	private JTextField OC_TEST_FILE_COLUMN;
	private JTextArea OC_SQLS;
	private JTextArea OC_DATAS;
	private JTextArea OC_STATUS;
	private JCheckBox OC_PARALLEL;
	JComboBox combo;
	String option[] = {"파일로 읽기", "직접 데이터 입력", "랜덤변수생성"};
	
	public PostgresPluginGUI() {
		combo  = new JComboBox(option);
		    
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setSize(800, 800);
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        GridBagConstraints editConstraints = new GridBagConstraints();
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.weightx = 1.0;
        editConstraints.fill = GridBagConstraints.HORIZONTAL;
        
        init(mainPanel, labelConstraints, editConstraints);
        
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
	}
	
	@Override
	public void configure(TestElement element) {
		super.configure(element);
	}

	@Override
	public String getLable() {
		return "Postgres 테스트 플러그인";
	}

	@Override
	public void initFields() {
		logger.debug("init Field");
	}

	@Override
	public void clearGui() {
		super.clearGui();
		initFields();
	}

	protected void configureTestElement(TestElement mc) {
		super.configureTestElement(mc);
		if (mc instanceof PostgresSampler) {
			PostgresSampler cSampler = (PostgresSampler) mc;
		}
	}

	@Override
	public void init(JPanel mainPanel, GridBagConstraints labelConstraints, GridBagConstraints editConstraints) {
		addToPanel(mainPanel, labelConstraints, 0, 1, new JLabel("연결 이름: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 1, OC_NAME = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 2, new JLabel("연결 URL: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 2, OC_URL = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 3, new JLabel("연결 Driver: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 3, OC_DRIVER = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 4, new JLabel("연결 최소 갯수: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 4, OC_MIN = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 5, new JLabel("연결 최대 갯수: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 5, OC_MAX = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 6, new JLabel("연결 아이디: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 6, OC_ID = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 7, new JLabel("연결 비밀번호: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 7, OC_PW = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 8, new JLabel("서비스 이름: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 8, OC_SVC_NAME = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 9, new JLabel("테스트 파일 경로: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 9, OC_TEST_FILE_PATH = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 10, new JLabel("테스트 파일 칼럼명: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 10, OC_TEST_FILE_COLUMN = new JTextField(10));
		addToPanel(mainPanel, labelConstraints, 0, 11, new JLabel("테스트 데이터 종류: ", JLabel.RIGHT));
		addToPanel(mainPanel, labelConstraints, 0, 12, new JLabel("병렬처리여부: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 12, OC_PARALLEL = new JCheckBox("병렬처리함"));
		addToPanel(mainPanel, labelConstraints, 0, 13, new JLabel("SQL: ", JLabel.RIGHT));
		OC_SQLS = new JTextArea(7,10);
		OC_SQLS.setWrapStyleWord(true);
		OC_SQLS.setLineWrap(true);
		addToPanel(mainPanel, editConstraints, 1, 13, OC_SQLS);
		addToPanel(mainPanel, labelConstraints, 0, 14, new JLabel("테스트 데이터: ", JLabel.RIGHT));
		OC_DATAS = new JTextArea(7,10);
		OC_DATAS.setWrapStyleWord(true);
		OC_DATAS.setLineWrap(true);
		addToPanel(mainPanel, editConstraints, 1, 14,OC_DATAS);
		
		StringBuilder sb = new StringBuilder();
		sb.append("select  CRDID, TX_DTM, TX_NTV_NO, MCTNO, AP_AMT, VAR_ID_01, VAR_ID_02, VAR_ID_03, VAR_ID_04, VAR_ID_05, VAR_NUM_01, VAR_NUM_02, VAR_NUM_03, VAR_NUM_04, VAR_NUM_05, VAR_CTG_01, VAR_CTG_02, VAR_CTG_03, VAR_CTG_04, VAR_CTG_05, VAR_CTG_06, VAR_CTG_07, VAR_CTG_08, VAR_CTG_09, VAR_CTG_10, VAR_CTG_11, VAR_CTG_12, VAR_CTG_13, VAR_CTG_14, VAR_CTG_15, VAR_CTG_16, VAR_CTG_17, VAR_CTG_18, VAR_CTG_19, VAR_CTG_20, VAR_CTG_21, VAR_CTG_22, VAR_CTG_23, VAR_CTG_24, VAR_CTG_25, VAR_CTG_26, VAR_CTG_27, VAR_CTG_28, VAR_CTG_29, VAR_CTG_30 from sf_tx_crd_tx_bs  where CRDID = #{CRDID}  and TX_DTM <= #{TX_DTM};\n");
				
		this.OC_URL.setText("jdbc:postgresql://10.8.61.183:5432/postgres");
		this.OC_DRIVER.setText("org.postgresql.Driver");
		this.OC_TEST_FILE_PATH.setText("Enter full path of file");
		this.OC_TEST_FILE_COLUMN.setText("Enter info like column1,column2,column3.....");
		this.OC_NAME.setText("Postgres Plugin Test Connection");
		this.OC_ID.setText("postgres");
		this.OC_PW.setText("asdfasdf11");
		this.OC_SQLS.setText(sb.toString());
		this.OC_TEST_FILE_COLUMN.setText("CRDID, TX_DTM, TX_NTV_NO, MCTNO, AP_AMT, VAR_ID_01, VAR_ID_02, VAR_ID_03, VAR_ID_04, VAR_ID_05, VAR_NUM_01, VAR_NUM_02, VAR_NUM_03, VAR_NUM_04, VAR_NUM_05, VAR_CTG_01, VAR_CTG_02, VAR_CTG_03, VAR_CTG_04, VAR_CTG_05, VAR_CTG_06, VAR_CTG_07, VAR_CTG_08, VAR_CTG_09, VAR_CTG_10, VAR_CTG_11, VAR_CTG_12, VAR_CTG_13, VAR_CTG_14, VAR_CTG_15, VAR_CTG_16, VAR_CTG_17, VAR_CTG_18, VAR_CTG_19, VAR_CTG_20, VAR_CTG_21, VAR_CTG_22, VAR_CTG_23, VAR_CTG_24, VAR_CTG_25, VAR_CTG_26, VAR_CTG_27, VAR_CTG_28, VAR_CTG_29, VAR_CTG_30");
		this.OC_DATAS.setText("{920077770(1000000~9999999)},{20200501(10~23)(10~59)(10~59)},{(10000000~99999999)}");
		this.OC_MIN.setText("70");
		this.OC_MAX.setText("80");
	}

	@Override
	public TestElement createTestElement() {
		PostgresSampler sampler = new PostgresSampler();
		modifyTestElement(sampler);
		initSampler(sampler);
		sampler.init();
		return sampler;
	}

	private void initSampler(PostgresSampler sampler) {
		sampler.setComment("Postgres Plugin Sampler");
		sampler.setClusterId(this.OC_ID.getText());
		sampler.setClusterName(this.OC_NAME.getText());
		sampler.setClusterPw(this.OC_PW.getText());
		sampler.setClusterUrl(this.OC_URL.getText());
		sampler.setSourcePath(this.OC_TEST_FILE_PATH.getText());
		sampler.setColumnNames(this.OC_TEST_FILE_COLUMN.getText());
		sampler.setMinPool(this.OC_MIN.getText());
		sampler.setMaxPool(this.OC_MAX.getText());
		if(this.OC_SQLS.getText() != null && !"".equals(this.OC_SQLS.getText())) {
			sampler.setStatements(this.OC_SQLS.getText());
		}
		if(this.OC_PARALLEL.isSelected()) {
			CassandraInstantPluginMap.getInstance().setParallel(true);
		}else {
			CassandraInstantPluginMap.getInstance().setParallel(false);
		}
		sampler.setInputDatas(this.OC_DATAS.getText());
	}

	@Override
	public void modifyTestElement(TestElement sampler) {
		this.configureTestElement(sampler);
		if (sampler instanceof PostgresSampler) {
			PostgresSampler cSampler = (PostgresSampler) sampler;
			this.initSampler(cSampler);
			cSampler.init();
		}
	}

}
