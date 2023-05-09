package com.auto.test.jmeter.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.common.data.FileJsonArrayList;
import com.auto.test.jmeter.plugin.common.data.TestMessageByCombination;

public class TestDataCreator {

	static Logger logger = LoggerFactory.getLogger(TestDataCreator.class);
	// {AAAA(1111111111111~9999999999999)}
	public static void main(String[] args) {
		String[][] meta = {
				{"cid","string","{AAAA(1111111111111~9999999999999)}","13","5","N"},
				{"request_date","datetime","-100~0,day,yyyymmdd","20","N","N"}
		};
		TestMessageByCombination comb = new TestMessageByCombination();
		comb.build(meta);
		comb.setStop(false);
		FileJsonArrayList data = comb.getFileMessage((d, c)->{
			logger.debug("data = " + d + ", count = " + c);
			return "";
		}, false);
		
	}
	
}
