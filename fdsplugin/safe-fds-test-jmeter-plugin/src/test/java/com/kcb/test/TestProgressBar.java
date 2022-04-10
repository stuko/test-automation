package com.kcb.test;

import com.kcb.fds.jmeter.plugin.common.gui.PluginProgressBar;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertTrue;

public class TestProgressBar {

    @Test
    void testProgress(){
        boolean result = false;
        try{
            JButton button = new JButton("click");
            TextField tf = new TextField();
            JFrame jf = new JFrame("TEST FRAME");
            jf.setLayout(new FlowLayout());
            jf.add(tf);
            jf.add(button);
            jf.setPreferredSize(new Dimension(300,300));

            button.addActionListener(event->{
                PluginProgressBar frame = new PluginProgressBar(100,1 , 1000, ()->{
                    return "".equals(tf.getText()) ? 0 : Integer.parseInt(tf.getText());
                });
                new Thread(()->{
                    frame.start();
                }).start();
            });

            jf.pack();
            jf.setVisible(true);
            jf.setLocationRelativeTo(null);
            result = true;
        }catch(Exception e){
            System.out.println(e.toString());
            result = false;
        }
        assertTrue(result);
    }
}
