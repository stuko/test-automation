package com.auto.test.jmeter.plugin.database.cassandra;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
 
public class CassandraInstantPluginMap {
	public static CassandraInstantPluginMap instance;
	public static Map<String,Object> map;
	public static Map<String,Cluster> clusterMap;
	public static Map<String,Session> sessionMap;
	private String[][] queryTime;
	private boolean isFakeData = false;
	private boolean isParallel = false;
	private CassandraQueryTimeListener listener;
	
	static {
		if(instance == null) instance = new CassandraInstantPluginMap(); 
	}
	
	private CassandraInstantPluginMap() {
		if(map == null) map = new HashMap<>();
		if(clusterMap == null) clusterMap = new HashMap<>();
		if(sessionMap == null) sessionMap = new HashMap<>();
	}
	
	public static CassandraInstantPluginMap getInstance() {
		if(instance == null) instance = new CassandraInstantPluginMap();
		return instance;
	}
	
	public CassandraQueryTimeListener getListener() {
		return listener;
	}

	public void setListener(CassandraQueryTimeListener listener) {
		this.listener = listener;
	}

	public String[][] getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(String[][] queryTime) {
		this.queryTime = queryTime;
		this.getListener().applyQueryTime(this.queryTime);
	}

	public void clearQueryTime() {
		this.queryTime = null;

	}
	
	public boolean isParallel() {
		return isParallel;
	}

	public void setParallel(boolean isParallel) {
		this.isParallel = isParallel;
	}

	public boolean isFakeData() {
		return isFakeData;
	}

	public void setFakeData(boolean isFakeData) {
		this.isFakeData = isFakeData;
	}

	public void set(String name, Object value) {
		map.put(name,value);
	}
	
	public Object get(String name) {
		return map.get(name);
	}
	
	public void setCluster(String name, Cluster value) {
		clusterMap.put(name,value);
	}
	
	public Cluster getCluster(String name) {
		return clusterMap.get(name);
	}
	
	public void setSession(String name, Session value) {
		sessionMap.put(name,value);
	}
	
	public Session getSession(String name) {
		return sessionMap.get(name);
	}
	
	
}
