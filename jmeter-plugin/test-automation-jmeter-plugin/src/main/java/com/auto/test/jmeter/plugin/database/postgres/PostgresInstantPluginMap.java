package com.auto.test.jmeter.plugin.database.postgres;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class PostgresInstantPluginMap {
	public static PostgresInstantPluginMap instance;
	public static Map<String,Object> map;
	public static Map<String,BasicDataSource> dataSourceMap;
	
	static {
		if(instance == null) instance = new PostgresInstantPluginMap(); 
	}
	
	private PostgresInstantPluginMap() {
		if(map == null) map = new HashMap<>();
		if(dataSourceMap == null) dataSourceMap = new HashMap<>();
	}
	
	public static PostgresInstantPluginMap getInstance() {
		if(instance == null) instance = new PostgresInstantPluginMap();
		return instance;
	}
	
	public void set(String name, Object value) {
		map.put(name,value);
	}
	
	public Object get(String name) {
		return map.get(name);
	}
	
	public void setDataSource(String name, BasicDataSource value) {
		dataSourceMap.put(name,value);
	}
	
	public BasicDataSource getDataSource(String name) {
		return dataSourceMap.get(name);
	}
	
}
