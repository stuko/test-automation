package com.kcb.fds.jmeter.plugin.common.factor.value;

import com.kcb.fds.jmeter.plugin.common.factor.TestPluginMessageFactorImplFactory;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginConstants;
import com.kcb.fds.jmeter.plugin.common.util.TestPluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.kcb.fds.jmeter.plugin.common.factor.define.TestPluginMessageSubstringFactor;

public class TestPluginMessageFactorFileValue extends AbstractTestPluginMessageFactorValueImpl {

    String fileName;
    private List<String> result = new ArrayList<>();
    static Logger logger = LoggerFactory.getLogger(TestPluginMessageFactorFileValue.class);
    public TestPluginMessageFactorFileValue(TestPluginMessageSubstringFactor factor) {
        this.setValueType(Type.File);
        this.setFdsPluginMessageFactor(factor);
        this.result.add(this.getFileRange());
    }

    @Override
    public List<String> getValues() {
        return getResult();
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public String toString(){
        String s = this.result.get(0);
        return s;
    }

    private String getFileRange() {
        this.fileName = this.getFdsPluginMessageFactor().getValue();
        StringBuilder sb = new StringBuilder();
        File f = new File(new File(TestPluginConstants.fds_test_define_path),fileName);
        if(!f.exists()){
            logger.error("File [{}] does not exist!!!!!!! ", f.getAbsolutePath());
            return null;
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(f.getAbsolutePath()), Charset.forName(TestPluginUtil.findFileEncoding(f)))){
            String line = null;
            TestPluginMessageFactorImplFactory factor = null;
            while ((line = br.readLine()) != null) {
                String[] rows = line.split("\t");
                if (rows.length == 5) {
                    logger.info("name={}, type={}, value={}, length={} , encode={}", rows[0], rows[1], rows[2], rows[3], rows[4]);
                    factor = new TestPluginMessageFactorImplFactory(rows[0], rows[1], rows[2], "Y".equals(rows[4]) ? true:false );
                    factor.setLength(rows[3]);
                } else if (rows.length == 6) {
                    logger.info("name={}, type={}, value={}, count={}, length={} , encode={}", rows[0], rows[1], rows[2], rows[3], rows[4], rows[5]);
                    factor = new TestPluginMessageFactorImplFactory(rows[0], rows[1], rows[2], Integer.parseInt(rows[3]),  "Y".equals(rows[5]) ? true:false);
                    factor.setLength(rows[4]);
                } else {
                    logger.info("data error................");
                }
                if(factor != null){
                    sb.append(factor.getLengthString(factor.getFactorRange().toString(),Integer.parseInt(factor.getLength())));
                }
                factor = null;
            }
        } catch (Exception e) {
            logger.error(e.toString(),e);
        }
        return sb.toString();
    }

}
