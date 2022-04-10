/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kcb.fds.plugin.test.result.data;

import com.kcb.fds.jmeter.plugin.common.data.FileJsonArrayListQueue;

/**
 *
 * @author O218001_D
 */
public interface TestResultDataSource {
    FileJsonArrayListQueue load(String folder, String path);
}
