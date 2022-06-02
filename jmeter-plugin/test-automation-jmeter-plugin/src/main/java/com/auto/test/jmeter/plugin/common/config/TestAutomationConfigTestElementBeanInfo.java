package com.auto.test.jmeter.plugin.common.config;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import java.beans.PropertyDescriptor;

public class TestAutomationConfigTestElementBeanInfo extends BeanInfoSupport {

    public static final String FILENAME = "filename";

    public TestAutomationConfigTestElementBeanInfo() {
        super(TestAutomationConfigTestElement.class);
        createPropertyGroup("Test_Automation_Config", new String[] {
                FILENAME
        });
        PropertyDescriptor p = property(FILENAME);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "./test/data/");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        // p.setPropertyEditorClass(FileEditor.class);
    }
}
