package com.auto.test.jmeter.plugin.database.cassandra;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.jmeter.gui.AbstractGUI;

public class CassandraPluginGUI extends AbstractGUI implements CassandraPluginCallBack {

	static Logger logger = LoggerFactory.getLogger(CassandraPluginGUI.class);
	
	private JTextField CSS_NAME;
	private JTextField CSS_URL;
	private JTextField CSS_LOCAL_POOL_CORE;
	private JTextField CSS_LOCAL_POOL_MAX;
	private JTextField CSS_REMOTE_POOL_CORE;
	private JTextField CSS_REMOTE_POOL_MAX;
	private JTextField CSS_ID;
	private JTextField CSS_PW;
	private JTextField CSS_KEY_SPACE;
	private JTextField CSS_TEST_FILE_PATH;
	private JTextField CSS_TEST_FILE_COLUMN;
	private JTextArea CSS_SQLS;
	private JTextArea CSS_DATAS;
	private JTextArea CSS_STATUS;
	private JCheckBox CSS_PARALLEL;
	final String combo_data[] = {"파일로 읽기", "직접 데이터 입력", "랜덤변수생성"};
	private final JComboBox<String> CSS_TEST_KIND  = new JComboBox<String>(combo_data); 
	
	public CassandraPluginGUI() {

        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        JPanel mainPanel = new JPanel(new GridBagLayout());
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
		return "카산드라 테스트 플러그인";
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
		logger.debug("configure test element");
		super.configureTestElement(mc);
		if (mc instanceof CassandraSampler) {
			logger.debug("configure test element - Cassandra");
			CassandraSampler cSampler = (CassandraSampler) mc;
		}
	}

	@Override
	public void init(JPanel mainPanel, GridBagConstraints labelConstraints, GridBagConstraints editConstraints) {
		addToPanel(mainPanel, labelConstraints, 0, 1, new JLabel("클러스터 이름: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 1, CSS_NAME = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 2, new JLabel("클러스터 URL: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 2, CSS_URL = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 3, new JLabel("클러스터 로컬 코어 연결 풀 갯수: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 3, CSS_LOCAL_POOL_CORE = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 4, new JLabel("클러스터 로컬 최대 연결 풀 갯수: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 4, CSS_LOCAL_POOL_MAX = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 5, new JLabel("클러스터 리모트 코어 연결 풀 갯수: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 5, CSS_REMOTE_POOL_CORE = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 6, new JLabel("클러스터 리모트 최대 연결 풀 갯수: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 6, CSS_REMOTE_POOL_MAX = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 7, new JLabel("클러스터 아이디: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 7, CSS_ID = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 8, new JLabel("클러스터 패스워드: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 8, CSS_PW = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 9, new JLabel("키 스페이스 이름: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 9, CSS_KEY_SPACE = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 10, new JLabel("테스트 파일 경로: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 10, CSS_TEST_FILE_PATH = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 11, new JLabel("테스트 파일 칼럼명: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 11, CSS_TEST_FILE_COLUMN = new JTextField());
		addToPanel(mainPanel, labelConstraints, 0, 12, new JLabel("테스트 데이터 종류: ", JLabel.RIGHT));
		addToPanel(mainPanel, labelConstraints, 0, 13, new JLabel("병렬처리여부: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 13, CSS_PARALLEL = new JCheckBox("병렬처리함"));
		addToPanel(mainPanel, labelConstraints, 0, 14, new JLabel("SQL: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 14, CSS_SQLS = new JTextArea(7,20));
		addToPanel(mainPanel, labelConstraints, 0, 15, new JLabel("테스트 데이터: ", JLabel.RIGHT));
		addToPanel(mainPanel, editConstraints, 1, 15, CSS_DATAS = new JTextArea(7,20));
		
		StringBuilder sb = new StringBuilder();
		sb.append("select  CRDID, TX_DTM, TX_NTV_NO, MCTNO, AP_AMT, VAR_ID_01, VAR_ID_02, VAR_ID_03, VAR_ID_04, VAR_ID_05, VAR_NUM_01, VAR_NUM_02, VAR_NUM_03, VAR_NUM_04, VAR_NUM_05, VAR_CTG_01, VAR_CTG_02, VAR_CTG_03, VAR_CTG_04, VAR_CTG_05, VAR_CTG_06, VAR_CTG_07, VAR_CTG_08, VAR_CTG_09, VAR_CTG_10, VAR_CTG_11, VAR_CTG_12, VAR_CTG_13, VAR_CTG_14, VAR_CTG_15, VAR_CTG_16, VAR_CTG_17, VAR_CTG_18, VAR_CTG_19, VAR_CTG_20, VAR_CTG_21, VAR_CTG_22, VAR_CTG_23, VAR_CTG_24, VAR_CTG_25, VAR_CTG_26, VAR_CTG_27, VAR_CTG_28, VAR_CTG_29, VAR_CTG_30 from source.sf_tx_crd_tx_bs  where CRDID = #{CRDID}  and TX_DTM <= #{TX_DTM};\n");
		
		this.CSS_URL.setText("Enter info like node:port,node:port,node:port.....");
		this.CSS_TEST_FILE_PATH.setText("Enter full path of file");
		this.CSS_TEST_FILE_COLUMN.setText("Enter info like column1,column2,column3.....");
		
		this.CSS_URL.setText("192.168.57.160:9042,192.168.57.161:9042,192.168.57.168:9042,192.168.57.169:9042");
		this.CSS_NAME.setText("Test Cluster");
		this.CSS_SQLS.setText(sb.toString());
		this.CSS_KEY_SPACE.setText("source");
		this.CSS_TEST_FILE_COLUMN.setText("CRDID, TX_DTM, TX_NTV_NO, MCTNO, AP_AMT, VAR_ID_01, VAR_ID_02, VAR_ID_03, VAR_ID_04, VAR_ID_05, VAR_NUM_01, VAR_NUM_02, VAR_NUM_03, VAR_NUM_04, VAR_NUM_05, VAR_CTG_01, VAR_CTG_02, VAR_CTG_03, VAR_CTG_04, VAR_CTG_05, VAR_CTG_06, VAR_CTG_07, VAR_CTG_08, VAR_CTG_09, VAR_CTG_10, VAR_CTG_11, VAR_CTG_12, VAR_CTG_13, VAR_CTG_14, VAR_CTG_15, VAR_CTG_16, VAR_CTG_17, VAR_CTG_18, VAR_CTG_19, VAR_CTG_20, VAR_CTG_21, VAR_CTG_22, VAR_CTG_23, VAR_CTG_24, VAR_CTG_25, VAR_CTG_26, VAR_CTG_27, VAR_CTG_28, VAR_CTG_29, VAR_CTG_30");
		this.CSS_DATAS.setText("{920077770(1000000~9999999)},{20200501(10~23)(10~59)(10~59)},{(10000000~99999999)}");
		this.CSS_LOCAL_POOL_CORE.setText("4");
		this.CSS_LOCAL_POOL_MAX.setText("8");
		this.CSS_REMOTE_POOL_CORE.setText("2");
		this.CSS_REMOTE_POOL_MAX.setText("4");
	}

	@Override
	public TestElement createTestElement() {
		CassandraSampler sampler = new CassandraSampler();
		modifyTestElement(sampler);
		initSampler(sampler);
		sampler.init();
		return sampler;
	}

	private void initSampler(CassandraSampler sampler) {
		sampler.setComment("Cassandra Plugin Sampler");
		sampler.setClusterId(this.CSS_ID.getText());
		sampler.setClusterName(this.CSS_NAME.getText());
		sampler.setClusterPw(this.CSS_PW.getText());
		sampler.setClusterUrl(this.CSS_URL.getText());
		sampler.setSourcePath(this.CSS_TEST_FILE_PATH.getText());
		sampler.setColumnNames(this.CSS_TEST_FILE_COLUMN.getText());
		sampler.setKeySpace(this.CSS_KEY_SPACE.getText());
		sampler.setLocalCorePool(this.CSS_LOCAL_POOL_CORE.getText());
		sampler.setLocalMaxPool(this.CSS_LOCAL_POOL_MAX.getText());
		sampler.setRemoteCorePool(this.CSS_REMOTE_POOL_CORE.getText());
		sampler.setRemoteMaxPool(this.CSS_REMOTE_POOL_MAX.getText());
		if(this.CSS_SQLS.getText() != null && !"".equals(this.CSS_SQLS.getText())) {
			sampler.setStatements(this.CSS_SQLS.getText());
		}
		sampler.setInputDatas(this.CSS_DATAS.getText());
		if(this.CSS_PARALLEL.isSelected()) {
			CassandraInstantPluginMap.getInstance().setParallel(true);
		}else {
			CassandraInstantPluginMap.getInstance().setParallel(false);
		}
		sampler.setCallBack(this);
	}

	@Override
	public void modifyTestElement(TestElement sampler) {
		this.configureTestElement(sampler);
		if (sampler instanceof CassandraSampler) {
			CassandraSampler cSampler = (CassandraSampler) sampler;
			this.initSampler(cSampler);
			cSampler.init();
		}
	}

	@Override
	public String call(String data) {
		this.CSS_STATUS.setText(this.CSS_STATUS.getText() + data + "\n");
		return data;
	}

}
