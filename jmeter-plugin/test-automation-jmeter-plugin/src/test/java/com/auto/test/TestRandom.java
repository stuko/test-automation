/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.auto.test;

import com.auto.test.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import org.junit.jupiter.api.Test;
import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
/**
 *
 * @author O218001_D
 */
public class TestRandom {
    @Test
    void testRandom(){
        System.out.println("Test Random >>>>>>>>>>>>>>");
        System.out.println(TestPluginMessageFactorImplFactory.getRandomCharacter(TestPluginConstants.ta_random_char_length).toString());
    }
}
