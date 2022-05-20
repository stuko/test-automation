package com.auto.test.jmeter.plugin.common.listener;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.testelement.AbstractTestElement;

import java.io.Serializable;

public class PluginTestListener extends AbstractTestElement implements Serializable, SampleListener, TestListener {

    static Logger logger = LoggerFactory.getLogger(PluginTestListener.class);

    @Override
    public void addError(Test test, Throwable e) {
        logger.error("Error Occurred " + test.toString());
        System.out.println("Error Occurred " + test.toString());
    }

    @Override
    public void addFailure(Test test, AssertionFailedError e) {
        logger.error("Failure Occurred " + test.toString());
        System.out.println("Failure Occurred " + test.toString());
    }

    @Override
    public void endTest(Test test) {
        logger.error("End Occurred " + test.toString());
        System.out.println("End Occurred " + test.toString());
    }

    @Override
    public void startTest(Test test) {
        logger.error("Start Occurred " + test.toString());
        System.out.println("Start Occurred " + test.toString());
    }

    @Override
    public void sampleOccurred(SampleEvent e) {

        logger.error("Sample Occurred " + e.toString());
        System.out.println("Sample Occurred " + e.toString());
    }

    @Override
    public void sampleStarted(SampleEvent e) {
        logger.error("Sample Started " + e.toString());
        System.out.println("Sample Started " + e.toString());
    }

    @Override
    public void sampleStopped(SampleEvent e) {
        logger.error("Sample Stopped " + e.toString());
        System.out.println("Sample Stopped " + e.toString());
    }


}
