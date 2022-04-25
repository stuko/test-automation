package com.auto.test.jmeter.plugin.common.gui;

import com.auto.test.jmeter.plugin.common.sampler.TestPluginSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import com.auto.test.jmeter.plugin.common.function.TestPluginCallBack;

public abstract class AbstractTestAutomationPluginGui  extends AbstractSamplerGui{

    static Logger logger = LoggerFactory.getLogger(AbstractTestAutomationPluginGui.class);
    private TestExplainPanel testExplainPanel = null;
    private TestRunConfigPanel testRunConfigPanel = null;
    private TestDataConfigPanel testDataConfigPanel = null;
    TestPluginCallBack callback;
    
    public AbstractTestAutomationPluginGui(){
        try{
            display();
            testRunConfigPanel.attach();
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    // JMETER�� Sampler �׸� �޴��� �������� �̸��Դϴ�.
    // ���⿡ �̸����,  JMETER���� �������� �˴ϴ�.
    @Override
    public String getStaticLabel() {
        return getLabelResource();
    }
    public void display() {
        this.display(null);
    }
    public void display(TestPluginCallBack callback) {
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(800,600));
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        JPanel container = new JPanel(new BorderLayout());
        
        
        // Test Data Config Panel
        // TestDataConfigPanel > southPanel > container(SOUTH)
        testDataConfigPanel = new TestDataConfigPanel(this.getLabelResource());
        
        testExplainPanel = new TestExplainPanel();
        testExplainPanel.initComponent();
        
        southPanel.add(testExplainPanel,BorderLayout.NORTH);
        southPanel.add(getTestDataConfigPanel(),BorderLayout.CENTER);
  

        // Test Run Config Panel
        // TestRunConfigPanel > container(NORTH)
        testRunConfigPanel = new TestRunConfigPanel(testDataConfigPanel,null);
        container.add(testRunConfigPanel, BorderLayout.CENTER);

        container.add(southPanel, BorderLayout.SOUTH);
        container.setBackground(Color.WHITE);
        add(container, BorderLayout.PAGE_START);
    }

    // �׽�Ʈ ���(������)�� ���� ����� �ִ� �޼��� �Դϴ�.
    // Sampler ��ü�� ���Ͽ� ��� �մϴ�.
    // Sampler�� �׽�Ʈ �����Ϳ� �׽�Ʈ ���� ��ü�� �����մϴ�.
    // FdsPluginSampler�� FdsPluginPanel ��ü �ȿ� ���� �մϴ�.
    // FdsPluginPanel�� Fds �׽�Ʈ �����͸� ������ �ִ� ������ �ϹǷ�,
    // FdsPluginSampler�� ����Ǿ�� �մϴ�.
    // Plugin GUI �� ���� ������ ��, Sampler �� �����ؼ� �������ִ� ������ �մϴ�.
    @Override
    public TestElement createTestElement() {
        logger.info("##### createTestElement #####");
        testRunConfigPanel.attach();
        this.configureTestElement(getTestDataConfigPanel().getSampler());
        return getTestDataConfigPanel().getSampler();
    }

    // Plugin GUI ���� ����� ������, Sampler�� ������ �ִ� ������ �մϴ�.
    @Override
    public void modifyTestElement(TestElement element) {
        logger.info("##### modifyTestElement #####");
        super.configureTestElement(element);
        if(element instanceof TestPluginSampler) {
            // Sampler�� �����Ǿ� ���ο� ��ü �׽�Ʈ ��Ҹ�
            // FdsPluginPanel�� ������ �ش�.
            // GUI���� ����� ������ Sampler�� ������ �ִ� ���
            // FdsPluginPanel�� �׻� ���ο� �ֱ��� Sampler�� �����ϰ� �ȴ�.
            getTestDataConfigPanel().setSampler((TestPluginSampler) element);
            testRunConfigPanel.attach();
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
    }

    @Override
    public void clearGui() {
        super.clearGui();
    }

    public TestDataConfigPanel getTestDataConfigPanel() {
        return testDataConfigPanel;
    }
    public void setTestDataConfigPanel(TestDataConfigPanel testDataConfigPanel) {
        this.testDataConfigPanel = testDataConfigPanel;
    }
    public void setTestCount(long cnt){
       this.testDataConfigPanel.setTestCount(cnt);
   }
    public TestRunConfigPanel getTestRunConfigPanel(){
        return testRunConfigPanel;
    }
    
}
