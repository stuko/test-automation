package com.auto.test;

import com.auto.test.jmeter.plugin.common.util.TestPluginConstants;
import org.junit.jupiter.api.Test;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFileOpen {
    @Test
    void testFileOpen(){
        File f = new File(new File(TestPluginConstants.ta_test_define_path),TestPluginConstants.ta_test_define_file);
        System.out.println(f.getAbsolutePath());
        System.out.println(f.exists());
        try {
            System.out.println(findFileEncoding(f));
            Charset.forName(findFileEncoding(f));
        }catch(Exception e){
            e.printStackTrace();
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(f.getAbsolutePath()), Charset.forName(findFileEncoding(f)))){
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }
        // (5)
        detector.reset();
        return encoding;
    }
}
