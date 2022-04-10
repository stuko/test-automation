package com.kcb.fds.jmeter.plugin.common.gui;

import com.kcb.fds.jmeter.plugin.common.sampler.TestPluginSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import com.kcb.fds.jmeter.plugin.common.function.TestPluginCallBack;

public abstract class AbstractFdsPluginGui  extends AbstractSamplerGui{

    static Logger logger = LoggerFactory.getLogger(AbstractFdsPluginGui.class);
    private TestExplainPanel testExplainPanel = null;
    private TestRunConfigPanel testRunConfigPanel = null;
    private TestDataConfigPanel testDataConfigPanel = null;
    TestPluginCallBack callback;
    
    public AbstractFdsPluginGui(){
        try{
            display();
            testRunConfigPanel.attach();
        }catch(Exception e){
            logger.error(e.toString(),e);
        }
    }

    // JMETER의 Sampler 항목 메뉴에 보여지는 이름입니다.
    // 여기에 이름대로,  JMETER에서 보여지게 됩니다.
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

    // 테스트 요소(데이터)를 최초 만들어 주는 메서드 입니다.
    // Sampler 객체를 리턴에 줘야 합니다.
    // Sampler은 테스트 데이터와 테스트 실행 객체를 관리합니다.
    // FdsPluginSampler는 FdsPluginPanel 객체 안에 존재 합니다.
    // FdsPluginPanel은 Fds 테스트 데이터를 생성해 주는 역할을 하므로,
    // FdsPluginSampler와 연결되어야 합니다.
    // Plugin GUI 가 최초 생성될 때, Sampler 를 생성해서 리턴해주는 역할을 합니다.
    @Override
    public TestElement createTestElement() {
        logger.info("##### createTestElement #####");
        testRunConfigPanel.attach();
        this.configureTestElement(getTestDataConfigPanel().getSampler());
        return getTestDataConfigPanel().getSampler();
    }

    // Plugin GUI 에서 변경된 내용을, Sampler에 전달해 주는 역할을 합니다.
    @Override
    public void modifyTestElement(TestElement element) {
        logger.info("##### modifyTestElement #####");
        super.configureTestElement(element);
        if(element instanceof TestPluginSampler) {
            // Sampler가 복제되어 새로운 객체 테스트 요소를
            // FdsPluginPanel에 전달해 준다.
            // GUI에서 변경된 내용이 Sampler에 영향이 있는 경우
            // FdsPluginPanel은 항상 새로운 최근의 Sampler를 참조하게 된다.
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
