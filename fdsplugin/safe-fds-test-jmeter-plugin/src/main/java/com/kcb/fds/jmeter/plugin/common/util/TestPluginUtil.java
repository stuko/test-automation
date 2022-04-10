package com.kcb.fds.jmeter.plugin.common.util;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TestPluginUtil {
    static Logger logger = LoggerFactory.getLogger(TestPluginUtil.class);
    public static String findFileEncoding(File file) throws IOException {
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis = new java.io.FileInputStream(file);
        // (1)
        UniversalDetector detector = new UniversalDetector(null);
        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();
        // (4)
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            logger.info("Detected encoding = " + encoding);
        } else {
            logger.info("No encoding detected.");
            return "UTF-8";
        }
        // (5)
        detector.reset();
        return encoding;
    }

    public static String getSubstringData(String data){
        data = data.substring(data.indexOf("(")+1,data.indexOf(")"));
        return data;
    }

}
