/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kcb.fds.jmeter.plugin.common.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;

/**
 *
 * @author O218001_D
 */
public class TestExplainPanel  extends PluginGridPanel{
    
    public void initComponent(){
        this.setPreferredSize(new Dimension(600,170));
        this.setLayout(new GridBagLayout());

        addLabelInGridBack(0,0,"사용 예시 : ", this);
        addLabelInGridBack(0,1,"XXXXX,YYYYY,ZZZZZ  or  @SUBSTR(XXXXX) FROM(0) TO(3)", this);
        addLabelInGridBack(0,2,"111111~22222,33333~44444", this);
        addLabelInGridBack(0,3,"0,day,yyyymmddhhmmss or -100~0,day,yyyyMMdd", this);
        addLabelInGridBack(0,4,"20211010~20211231,day,yyyyMMdd or @SUBSTR(0,day,yyyymmddhhmmss) FROM(0) TO(4)", this);
        addLabelInGridBack(0,5,"{XXXX(1~2)YYYY|XXXX(1~2)YYYY|XXXX(1~2)YYYY|XXXX(1~2)YYYY}", this);
        addLabelInGridBack(0,6,"{(20200101~20200330)}", this);
        addLabelInGridBack(0,7,"reference 타입 : 대상 name을 적어줌.", this);
    }
}
