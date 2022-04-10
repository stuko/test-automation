package com.kcb.fds.jmeter.plugin.common.function;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface CombinationCall {
    void call(List<int[]> combination_source, int[] combination_result , int col, int count);
}
