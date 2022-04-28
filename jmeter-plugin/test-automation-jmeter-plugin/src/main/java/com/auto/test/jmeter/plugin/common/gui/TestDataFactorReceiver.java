package com.auto.test.jmeter.plugin.common.gui;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface TestDataFactorReceiver {
	void receive_factor(List<Map<String,Object>> factor);
}
