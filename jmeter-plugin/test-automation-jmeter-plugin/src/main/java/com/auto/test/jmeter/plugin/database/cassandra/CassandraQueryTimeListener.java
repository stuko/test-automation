package com.auto.test.jmeter.plugin.database.cassandra;

public interface CassandraQueryTimeListener {
	public void applyQueryTime(String[][] data);
}
