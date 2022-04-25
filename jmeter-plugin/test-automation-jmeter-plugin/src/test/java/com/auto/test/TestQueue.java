/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.auto.test;

import java.util.LinkedList;
import org.junit.jupiter.api.Test;

/**
 *
 * @author O218001_D
 */
public class TestQueue {
 
    LinkedList<String>  testQueue = new LinkedList<>();
    
    @Test
    void testQueuePeek(){
        testQueue.add("First");
        testQueue.add("Second");
        
        System.out.println(testQueue.size());
        System.out.println(testQueue.get(0));
        System.out.println(testQueue.get(1));
        System.out.println(testQueue.get(0));
        System.out.println(testQueue.get(1));
        System.out.println(testQueue.get(0));
        System.out.println(testQueue.get(1));
        
    }
}
