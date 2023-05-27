package com.auto.test.jmeter.plugin.database.postgres;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.async.AsyncExecutorManager;
import com.auto.test.jmeter.plugin.database.cassandra.CassandraSampler;
import com.netflix.jmeter.utils.SystemUtils;

public class PostgresSampler extends CassandraSampler {

	static Logger logger = LoggerFactory.getLogger(PostgresSampler.class);
	Connection[] connections;
	
	public PostgresSampler() {
		super();
	}

	public String getMinPool() {
		return this.getProperty("MinPool").getStringValue();
	}

	public void setMinPool(String minPool) {
		this.setProperty("MinPool", minPool);
	}

	public String getMaxPool() {
		return this.getProperty("MaxPool").getStringValue();
	}

	public void setMaxPool(String maxPool) {
		this.setProperty("MaxPool", maxPool);
	}

	public void init() {
		this.setDatas(new ArrayList<>());
		this.setColumns(new ArrayList<>());

		logger.debug("Initing.....");

		if (!this.chkNull(this.getClusterName())) {
			logger.debug("ClusterName is null : " + this.getClusterName());
			return;
		}
		if (!this.chkNull(this.getClusterUrl())) {
			logger.debug("URL is null : " + this.getClusterUrl());
			return;
		}
		if (!this.chkNull(this.getColumnNames())) {
			logger.debug("ColumnNames is null : " + this.getColumnNames());
			return;
		}
		logger.debug("Init Success and preparing inputdata ....");
		if (this.getInputDatas() != null && this.getInputDatas().length() > 0) {
			logger.debug("Text input data will be used....");
			this.readInputData();
		} else {
			if (this.getSourcePath() != null) {
				logger.debug("File input data will be used....");
				File f = new File(this.getSourcePath());
				if (!f.exists()) {
					logger.debug("File does not exist " + f.getAbsoluteFile());
					return;
				}
				this.readInputFile(f);
			}
		}
		logger.debug("Input data loading is OK...");
		try {
			AsyncExecutorManager.getINSTANCE().executeThread(()->{
				boolean stop = false;
				while(!stop) {
					try {
						this.connect(this.getClusterUrl(), this.getClusterId(), this.getClusterPw());
						stop = true;
					}catch(Exception e) {
						// logger.debug("Can not connect to DataBase.. So, wait 30 seconds and Retry....");
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							logger.error(e1.toString());
						}
					}			
				}
			});
		} catch (Exception e) {
			logger.debug(SystemUtils.getStackTrace(e));
		}
		logger.debug("Oracle connection is OK....");

	}

	public void connect(String url, String id, String pw) {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl(url);
		ds.setUsername(id);
		ds.setPassword(pw);
		ds.setMinIdle(Integer.parseInt(this.getMinPool()));
		ds.setMaxTotal(Integer.parseInt(this.getMaxPool()));
		PostgresInstantPluginMap.getInstance().setDataSource(this.getClusterId(), ds);
	}

	public Connection getConnection() throws SQLException {
		return this.getDataSource().getConnection();
	}

	public BasicDataSource getDataSource() {
		BasicDataSource bds = PostgresInstantPluginMap.getInstance().getDataSource(this.getClusterId());
		logger.debug("----- Connection Pool Info ---------");
		logger.debug("MaxIdle : " + bds.getMaxIdle());
		logger.debug("MaxTotal : " + bds.getMaxTotal());
		logger.debug("MinIdle : " + bds.getMinIdle());
		logger.debug("NumActive : " + bds.getNumActive());
		logger.debug("NumIdle : " + bds.getNumIdle());
		logger.debug("-----------------------------------");
		return bds;
	}

	public List<Map<String, Object>> executeQuery(Map<String, Object> data, String sql,
			Map<String, String> queryResponse) throws Exception {
		logger.debug("Oracle Exeucute Query........");
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		List<Map<String, Object>> result = new ArrayList<>();
		long start = System.currentTimeMillis();
		try {
			logger.debug("Oracle query[" + sql + "] will be executed.");
			logger.debug("Binding data is " + data);
			connection = this.getConnection();
			queryResponse.put("Connection Time", (System.currentTimeMillis() - start) + "");
			st = connection.createStatement();
			st.setFetchSize(200);
			queryResponse.put("Create Statement Time", (System.currentTimeMillis() - start) + "");
			String bindedSql = this.generateSql(sql, data);
			queryResponse.put("Generate SQL Time", (System.currentTimeMillis() - start) + "");
			if (bindedSql.toLowerCase().startsWith("select")) {
				rs = st.executeQuery(bindedSql);
				queryResponse.put("Execute Select Time", (System.currentTimeMillis() - start) + "");
				ResultSetMetaData rm = rs.getMetaData();
				while (rs.next()) {
					Map<String, Object> m = new HashMap<>();
					for (int i = 1; i <= rm.getColumnCount(); i++) {
						String value = rs.getString(rm.getColumnLabel(i));
						m.put(rm.getColumnLabel(i), value);
					}
					result.add(m);
				}
				queryResponse.put("Fetch Data Time", (System.currentTimeMillis() - start) + "");
				if (result != null && result.size() > 0)
					logger.debug("Query result = count : " + result.size() + " / first row : " + result.get(0));
				else
					logger.debug("Query result = count : 0 / first row : null");
			}else {
				int cnt = st.executeUpdate(bindedSql);
				queryResponse.put("Execute In/Up/Del Time", (System.currentTimeMillis() - start) + "");
				result.add(new HashMap<>());
				result.get(result.size() - 1).put("COUNT", cnt + "");
				queryResponse.put("Fetch Data(In/Up/Del) Time", (System.currentTimeMillis() - start) + "");
			}
			logger.debug("Query result : " + result);
		} catch (Exception e) {
			logger.error(e.toString(), e);
			result.add(new HashMap<>());
			result.get(result.size() - 1).put("ERROR", e.toString());
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ee) {
				result.add(new HashMap<>());
				result.get(result.size() - 1).put("ERROR", ee.toString());
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception ee) {
				result.add(new HashMap<>());
				result.get(result.size() - 1).put("ERROR", ee.toString());
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception ee) {
				result.add(new HashMap<>());
				result.get(result.size() - 1).put("ERROR", ee.toString());
			}
			
			long end = System.currentTimeMillis();
			queryResponse.put(sql, (end - start) + "");
		}
		return result;
	}

}
