package com.auto.test.jmeter.plugin.common.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import com.auto.test.jmeter.plugin.common.function.TestPluginProgressBarCallBack;

public class PluginProgressBar extends JFrame {
    static Logger logger = LoggerFactory.getLogger(PluginProgressBar.class);
    int MAX = 100;
    int INCREASE = 1; 
    long SLEEP = 1000L;
    JProgressBar current;
    int num = 0;
    TestPluginProgressBarCallBack callBack;

    public PluginProgressBar(int m, int inc, long sleep, TestPluginProgressBarCallBack callBack) {
        this.MAX = m;
        this.INCREASE = inc;
        this.SLEEP = sleep;
        this.callBack = callBack;
        current = new JProgressBar(0, m);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel pane = new JPanel();
        current.setValue(0);
        current.setStringPainted(true);
        pane.add(current);
        setContentPane(pane);
    }
    public void start(){
        current.setStringPainted(true);
        this.setLocationRelativeTo(null);
        current.setPreferredSize(new Dimension(340,40));
        this.setPreferredSize(new Dimension(400,100));
        current.setVisible(true);
        this.pack();
        this.setVisible(true);
        this.iterate();
    }

    public void iterate() {
        while (num < this.MAX) {
            current.setValue(this.callBack.getValue());
            try {
                // logger.info("progress bar value is {}", current.getValue()+"");
                Thread.sleep(this.SLEEP);
            } catch (InterruptedException e) {
            }
            num += this.INCREASE;
        }
    }

}
