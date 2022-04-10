package com.kcb.fds.jmeter.plugin.common.gui;

import javax.swing.*;
import java.awt.*;

public class PluginGridPanel extends JPanel{

    public void add(int x, int y, GridBagConstraints constraints, JComponent component){
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(component, constraints);
    }

    public void add(int x, int y, int weightx, int weighty, GridBagConstraints constraints, JComponent component){
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = weightx;
        constraints.gridheight = weighty;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(component, constraints);
    }
    
    public void add(int x, int y, int weightx, int weighty, int anchor,  GridBagConstraints constraints, JComponent component){
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = weightx;
        constraints.gridheight = weighty;
        constraints.anchor = anchor;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(component, constraints);
    }
    
    public void add(JPanel panel, int x, int y, int weightx, int weighty, int anchor,  GridBagConstraints constraints, JComponent component){
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = weightx;
        constraints.gridheight = weighty;
        constraints.anchor = anchor;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, constraints);
    }
    
    public void add(JPanel panel, int x, int y, int weightx, int weighty, int anchor, int fill, GridBagConstraints constraints, JComponent component){
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = weightx;
        constraints.gridheight = weighty;
        constraints.anchor = anchor;
        constraints.fill = fill;
        panel.add(component, constraints);
    }

    public GridBagConstraints getLabelGBC(){
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.EAST;
        return labelConstraints;
    }

    public GridBagConstraints getEditGBC(){
        GridBagConstraints editConstraints = new GridBagConstraints();
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.weightx = 1.0;
        // editConstraints.fill = GridBagConstraints.HORIZONTAL;
        return editConstraints;
    }

    public void addLabelInGridBack(int x, int y, String label, JPanel panel){
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = x;
        gc.gridy = y;
        panel.add(new JLabel(label),gc);
    }

}
