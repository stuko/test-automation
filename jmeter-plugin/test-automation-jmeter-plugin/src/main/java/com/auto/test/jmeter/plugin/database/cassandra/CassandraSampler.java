package com.auto.test.jmeter.plugin.database.cassandra;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.database.FakeData;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.google.gson.Gson;
import com.netflix.jmeter.utils.SystemUtils;

public class CassandraSampler extends org.apache.jmeter.samplers.AbstractSampler {

	static Logger logger = LoggerFactory.getLogger(CassandraSampler.class);
	
	ExecutorService executor = Executors.newFixedThreadPool(100);

	public static String MYBATIS_CONSTANT_VAR_START = "${";
	public static String MYBATIS_MAPPING_VAR_START = "#{";
	public static String MYBATIS_VAR_END = "}";

	CassandraPluginCallBack callBack;

	public CassandraSampler() {
		super();
	}

	public List<TableMetaFactor> getColumns() {
		return (List<TableMetaFactor>) CassandraInstantPluginMap.getInstance().get("Columns");
	}

	public void setColumns(List<TableMetaFactor> columns) {
		CassandraInstantPluginMap.getInstance().set("Columns", columns);
	}

	public CassandraPluginCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(CassandraPluginCallBack callBack) {
		this.callBack = callBack;
	}

	public String getInputDatas() {
		return this.getProperty("InputDatas").getStringValue();
	}

	public void setInputDatas(String inputDatas) {
		this.setProperty("InputDatas", inputDatas);
	}

	public boolean isParallel() {
		return CassandraInstantPluginMap.getInstance().isParallel();
	}

	public void setParallel(boolean parallel) {
		CassandraInstantPluginMap.getInstance().setParallel(parallel);
	}

	public String[] getStatements() {
		String ss = this.getProperty("Statements").getStringValue();
		return ss.split("[;]");
	}

	public void setStatements(String statements) {
		statements = statements.replace("\n", " ");
		statements = statements.trim();
		this.setProperty("Statements", statements);
	}

	public List<Map<String, Object>> getDatas() {
		return (List<Map<String, Object>>) CassandraInstantPluginMap.getInstance().get("Datas");
	}

	public void setDatas(List<Map<String, Object>> datas) {
		CassandraInstantPluginMap.getInstance().set("Datas", datas);
	}

	public String getSourcePath() {
		return this.getProperty("SourcePath").getStringValue();
	}

	public void setSourcePath(String sourcePath) {
		this.setProperty("SourcePath", sourcePath);
	}

	public String getClusterName() {
		return this.getProperty("ClusterName").getStringValue();
	}

	public void setClusterName(String clusterName) {
		this.setProperty("ClusterName", clusterName);
	}

	public String getClusterUrl() {
		return this.getProperty("ClusterUrl").getStringValue();
	}

	public void setClusterUrl(String clusterUrl) {
		this.setProperty("ClusterUrl", clusterUrl);
	}

	public String getClusterId() {
		return this.getProperty("ClusterId").getStringValue();
	}

	public void setClusterId(String clusterId) {
		this.setProperty("ClusterId", clusterId);
	}

	public String getClusterPw() {
		return this.getProperty("ClusterPw").getStringValue();
	}

	public void setClusterPw(String clusterPw) {
		this.setProperty("ClusterPw", clusterPw);
	}

	public String getLocalCorePool() {
		return this.getProperty("LocalCorePool").getStringValue();
	}

	public void setLocalCorePool(String localCorePool) {
		this.setProperty("LocalCorePool", localCorePool);
	}

	public String getLocalMaxPool() {
		return this.getProperty("LocalMaxPool").getStringValue();
	}

	public void setLocalMaxPool(String localMaxPool) {
		this.setProperty("LocalMaxPool", localMaxPool);
	}

	public String getRemoteCorePool() {
		return this.getProperty("RemoteCorePool").getStringValue();
	}

	public void setRemoteCorePool(String remoteCorePool) {
		this.setProperty("RemoteCorePool", remoteCorePool);
	}

	public String getRemoteMaxPool() {
		return this.getProperty("RemoteMaxPool").getStringValue();
	}

	public void setRemoteMaxPool(String remoteMaxPool) {
		this.setProperty("RemoteMaxPool", remoteMaxPool);
	}

	public String getKeySpace() {
		return this.getProperty("KeySpace").getStringValue();
	}

	public void setKeySpace(String keySpace) {
		this.setProperty("KeySpace", keySpace);
	}

	public String getColumnNames() {
		return this.getProperty("ColumnNames").getStringValue();
	}

	public void setColumnNames(String columnNames) {
		this.setProperty("ColumnNames", columnNames);
	}

	public void addColumn(String name, String type, boolean isPk) {
		this.getColumns().add(new TableMetaFactor(name, type, isPk));
	}

	public void flushTableColumns() {
		this.setColumns(new ArrayList<>());
	}

	public boolean chkNull(String data) {
		if (data != null && !"".equals(data))
			return true;
		else
			return false;
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
		if (!this.chkNull(this.getLocalCorePool())) {
			logger.debug("Local core pool is null : " + this.getLocalCorePool());
			return;
		}
		if (!this.chkNull(this.getLocalMaxPool())) {
			logger.debug("Local max pool is null :" + this.getLocalMaxPool());
			return;
		}
		if (!this.chkNull(this.getRemoteCorePool())) {
			logger.debug("Remote core pool is null :" + this.getRemoteCorePool());
			return;
		}
		if (!this.chkNull(this.getRemoteMaxPool())) {
			logger.debug("Remote Max pool is null : " + this.getRemoteMaxPool());
			return;
		}
		if (!this.chkNull(this.getKeySpace())) {
			logger.debug("KeySpace is null : " + this.getKeySpace());
			return;
		}
		logger.debug("Init Success and preparing inputdata ....");

		CassandraInstantPluginMap.getInstance().setFakeData(false);
		
		if (this.getInputDatas() != null && this.getInputDatas().length() > 0) {
			logger.debug("Text input data will be used....["+this.getInputDatas().substring(0,5)+"]");
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
		String[] urls = this.getClusterUrl().split("[,]");
		try {
			executor.execute(()-> {
				try {
					this.connectMany(urls);
				} catch (Exception e) {
					logger.error(e.toString(),e);
				}
			});
		} catch (Exception e) {
			logger.debug(SystemUtils.getStackTrace(e));
		}
		logger.debug("Cassandra connection is OK....");

	}

	public void readInputData() {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = new ByteArrayInputStream(this.getInputDatas().getBytes());
			br = new BufferedReader(new InputStreamReader(is));
			this.readerToData(br);
		} catch (Exception e) {
			logger.debug(SystemUtils.getStackTrace(e));
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ee) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ee) {
			}
		}
	}

	public void readInputFile(File f) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			this.setDatas(new ArrayList<>());

			fr = new FileReader(f);
			br = new BufferedReader(fr);
			readerToData(br);
		} catch (Exception e) {
			logger.debug(SystemUtils.getStackTrace(e));
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception ee) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ee) {
			}
		}
	}

	private void readerToData(BufferedReader br) throws IOException {
		String line = null;
		this.setDatas(new ArrayList<>());
		String[] sourceColumns = this.getColumnNames().split("[,]");
		int max = sourceColumns.length;

		while ((line = br.readLine()) != null) {
			
			logger.debug("Read line is : " + line);
			
			String[] cols = line.split("[,]");

			if (max > cols.length) {
				max = cols.length;
			}

			if (cols.length <= 1) {
				cols = line.split("[\t]");
			}
			if (cols.length <= 1) {
				cols = line.split("[\b]");
			}
			
			Map<String, Object> m = new HashMap<>();

			for (int i = 0; i < max; i++) {
				String c = cols[i].trim();
				if (c.startsWith("\"") && c.endsWith("\"")) {
					c = c.substring(1, c.length() - 1);
					m.put(sourceColumns[i].trim(), c);
				}else if (c.startsWith("{") && c.endsWith("}")){
					// FakeData Type
					logger.debug(c + " is Fake ? : " + CassandraInstantPluginMap.getInstance().isFakeData());
					CassandraInstantPluginMap.getInstance().setFakeData(true);
					c = c.substring(1, c.length() - 1);
					FakeData fd = new FakeData();
					fd.value(c);
					m.put(sourceColumns[i].trim(), fd);
				}else {
					m.put(sourceColumns[i].trim(), c);
				}
			}
			logger.debug("One map is : " + m);
			this.getDatas().add(m);
		}
		logger.debug("Read Data is Fake? : " + CassandraInstantPluginMap.getInstance().isFakeData());
	}

	private void sysoutFileContents(File f) {
		Path path = Paths.get(f.getAbsolutePath());
		if (Files.exists(path)) {
			try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
				ByteBuffer byteBuffer = ByteBuffer.allocate((int) Files.size(path));
				channel.read(byteBuffer);
				byteBuffer.flip();
				logger.debug(Charset.defaultCharset().decode(byteBuffer).toString());
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
	}

	public void connectMany(String[] url) throws Exception {
		PoolingOptions poolingOptions = new PoolingOptions();
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, Integer.parseInt(this.getLocalCorePool()))
				.setMaxConnectionsPerHost(HostDistance.LOCAL, Integer.parseInt(this.getLocalMaxPool()))
				.setCoreConnectionsPerHost(HostDistance.REMOTE, Integer.parseInt(this.getRemoteCorePool()))
				.setMaxConnectionsPerHost(HostDistance.REMOTE, Integer.parseInt(this.getRemoteMaxPool()));

		Builder b = Cluster.builder().withoutMetrics().withoutJMXReporting();
		for (String u : url) {
			String[] point = u.split(":");
			b.addContactPoint(point[0]).withPort(Integer.parseInt(point[1]));
		}
		
		if(this.getClusterId()!=null && !"".equals(this.getClusterId()) && this.getClusterPw()!=null &&  !"".equals(this.getClusterPw())) {
			b.withCredentials(this.getClusterId(), this.getClusterPw());
		}
		
		b.withPoolingOptions(poolingOptions);
		b.withQueryOptions(new QueryOptions()
		 .setConsistencyLevel(ConsistencyLevel.QUORUM));
		b.withSocketOptions(
           new SocketOptions()
          .setConnectTimeoutMillis(2000));
		Cluster cluster = b.build();
		CassandraInstantPluginMap.getInstance().setCluster("Cluster", cluster);
		Session session = cluster.connect();
		CassandraInstantPluginMap.getInstance().setSession("Session", session);

	} 

	public void connect(String node, Integer port) {
		PoolingOptions poolingOptions = new PoolingOptions();
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 4).setMaxConnectionsPerHost(HostDistance.LOCAL, 10)
				.setCoreConnectionsPerHost(HostDistance.REMOTE, 2).setMaxConnectionsPerHost(HostDistance.REMOTE, 4);

		Builder b = Cluster.builder().withoutMetrics().withoutJMXReporting().addContactPoint(node)
				.withPoolingOptions(poolingOptions);
		if (port != null) {
			b.withPort(port);
		}
		if(this.getClusterId()!=null && !"".equals(this.getClusterId()) && this.getClusterPw()!=null &&  !"".equals(this.getClusterPw())) {
			b.withCredentials(this.getClusterId(), this.getClusterPw());
		}

		Cluster cluster = b.build();
		CassandraInstantPluginMap.getInstance().setCluster("Cluster", cluster);
		Session session = cluster.connect();
		CassandraInstantPluginMap.getInstance().setSession("Session", session);
	}

	public Session getSession() {
		return CassandraInstantPluginMap.getInstance().getSession("Session");
	}

	public Cluster getCluster() {
		return CassandraInstantPluginMap.getInstance().getCluster("Cluster");
	}

	public void close() {
		getSession().close();
		getCluster().close();
	}

	public void createKeyspace(String keyspaceName, String replicationStrategy, int replicationFactor) {
		StringBuilder sb = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ").append(keyspaceName)
				.append(" WITH replication = {").append("'class':'").append(replicationStrategy)
				.append("','replication_factor':").append(replicationFactor).append("};");

		String query = sb.toString();
		getSession().execute(query);
	}

	public void createTable(String tableName) {
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
		this.getColumns().forEach(tm -> {
			String pk = tm.isPrimaryKey() ? "PRIMARY KEY" : "";
			sb.append(tm.getColumn() + " " + tm.getType() + " " + pk);
		});
		sb.append(");");
		String query = sb.toString();
		getSession().execute(query);
	}
	
	@Override
	public SampleResult sample(Entry entry) {
		SampleResult sr = new SampleResult();
		sr.setSampleLabel(getName());
		sr.sampleStart();
		sr.setDataType(SampleResult.TEXT);
		long start = sr.currentTimeInMillis();
		String message = "ERROR: UNKNOWN";
		Response response = null;
		try {
			response = execute();
			logger.debug("===== Query Time =====");
			logger.debug("Query count : " + response.getQueryTime().size());
			response.getQueryTime().forEach((k,v)->{
				logger.debug("Query Result : ["+k+"] ["+v+"]");
			});
			logger.debug("===== Query Time =====");
			
			sr.setBytes(response.size);
			message = "------------ Request --------------\n" +response.request + "\n" + "------------ Request --------------\n" + "------------ Response Body --------------\n" + response.getMessage() + "\n------------ Response Body --------------\n";
			sr.setResponseData(response.getRequest().getBytes());
			sr.setSuccessful(true);
			sr.setResponseCodeOK();
			sr.setResponseHeaders(response.request);
			sr.setResponseMessage(new Gson().toJson(response.getQueryTime()));
		} catch (Exception ex) {
			ex.printStackTrace();
			message = SystemUtils.getStackTrace(ex);
			sr.setSuccessful(false);
		} finally {
			sr.setResponseData(message.getBytes());
			long latency = System.currentTimeMillis() - start;
			sr.sampleEnd();
			sr.setLatency(latency);

			if (response != null && response.latency_in_ms != 0)
				sr.setIdleTime(latency - response.latency_in_ms);
		}
		return sr;
	}

	public int getRandom() {
		if (this.getDatas().size() == 0)
			return -1;
		return ((int) (Math.random() * 100000)) % this.getDatas().size();
	}

	public abstract class CallableQuery implements Callable<List<Map<String, Object>>> {
		@Override
		public abstract List<Map<String, Object>> call() throws Exception;
	} 

	public Response execute() throws Exception {
		Response res = new Response();
		Map<String,String> queryResponse = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		
		if (this.getDatas() != null && this.getDatas().size() > 0 && this.getStatements() != null) {
			List<Future<List<Map<String, Object>>>> futureList = new ArrayList<>();
			int rnd = -1;
			
			final Map<String, Object> data = new HashMap<>();
			if(CassandraInstantPluginMap.getInstance().isFakeData()) {
				this.getDatas().forEach(m->{
					m.forEach((k,v)->{
						data.put(k,((FakeData)v).value());
					});
				});
			}else {
				rnd = this.getRandom();
				if (rnd == -1) {
					res.setMessage("Test Data is exhausted. No more data...");
					res.setRequest("NULL");
					return res;
				}
				this.getDatas().get(rnd).forEach((k,v)->{
					data.put(k,v);
				});
			}
			logger.debug("Original data is " + data);

			for (String sql : this.getStatements()) {
				logger.debug("Original sql is " + sql);
				List<Map<String, Object>> result = new ArrayList<>();
				if(this.isParallel()) {
					logger.debug("Parallel Execute .....");
					try {
						Future<List<Map<String, Object>>> future = executor.submit(new CallableQuery(){
							public List<Map<String, Object>> call() throws Exception {
								List<Map<String, Object>> result = executeQuery(data, sql.trim() ,queryResponse);
								return result;
							}
						});
					}catch(Exception e) {
						logger.debug("Exception of Execute Query : " + e.toString());
						result.add(new HashMap<>());
						result.get(result.size()-1).put("ERROR",e.toString());
					}
				}else { 
					try {
						logger.debug("Sync. Execute .....");					
						result = this.executeQuery(data, sql.trim(),queryResponse);
					}catch(Exception e) {
						logger.debug("Exception of Execute Query : " + e.toString());
						result.add(new HashMap<>());
						result.get(result.size()-1).put("ERROR",e.toString());
					}
				}
			
				logger.debug("Result is " + result);
				res.setMessage((res.getMessage()==null ? "" : res.getMessage()) + "\nSQL["+sql+"] Result Start \n" + result + "\n---------- SQL Result End ---------\n");
				res.setRequest((res.getRequest()==null ? "" : res.getRequest()) + "\n" + data + "");
			}
			
			if(!CassandraInstantPluginMap.getInstance().isFakeData()) {
				this.getDatas().remove(rnd);
			}

			if(this.isParallel()) {
				for(Future<List<Map<String, Object>>> f : futureList) {
					List<Map<String, Object>> result = f.get();
				}
				executor.shutdown();
			}
		}
		
		if (res.getMessage() != null)
			res.setSize(res.getMessage().length());
		else
			res.setSize(0);
		res.setQueryTime(queryResponse);
		return res;
	}

	public List<Map<String, Object>> executeQuery(Map<String, Object> data, String sql , Map<String,String> queryResponse) throws Exception {
		logger.debug("Cassandra Exeucute Query........");
		long start = System.currentTimeMillis();
		ResultSet rs = this.getSession().execute(generateSql(sql, data));
		
		if (rs != null) {
			List<Map<String, Object>> result = new ArrayList<>();
			rs.forEach(r -> {
				Map<String, Object> m = new HashMap<>();
				r.getColumnDefinitions().forEach(d -> {
					if (d.getType() == DataType.text())
						m.put(d.getName(), r.get(d.getName(), String.class));
					else if (d.getType() == DataType.bigint())
						m.put(d.getName(), r.get(d.getName(), Long.class));
					else if (d.getType() == DataType.decimal())
						m.put(d.getName(), r.get(d.getName(), BigInteger.class));
					else if (d.getType() == DataType.cint()	|| d.getType() == DataType.tinyint())
						m.put(d.getName(), r.get(d.getName(), Integer.class));
					else if (d.getType() == DataType.varchar())
						m.put(d.getName(), r.get(d.getName(), String.class));
					else if (d.getType() == DataType.timestamp())
						m.put(d.getName(), r.get(d.getName(), Date.class));
					else
						m.put(d.getName(), r.get(d.getName(), Object.class));
				});
				result.add(m);
			});
			if(result != null && result.size() > 0)
				logger.debug("Query result = count : " + result.size() + " / first row : " + result.get(0));
			else
				logger.debug("Query result = count : 0 / first row : null");
			
			long end = System.currentTimeMillis();
			queryResponse.put(sql,(end - start) + "");
			
			return result;
		}else {
			logger.debug("Query result is NULL of SQL : ["+sql+"]");
		}
		long end = System.currentTimeMillis();
		queryResponse.put(sql,(end - start) + "");
		
		return null;
	}

	public String generateSql(String sql, Map<String, Object> param) throws Exception {
		if (param == null || param.size() == 0)
			return sql;
		int s = sql.indexOf(MYBATIS_CONSTANT_VAR_START);
		while (s >= 0) {
			int e = sql.indexOf(MYBATIS_VAR_END, s);
			sql = sql.substring(0, s) + param.get(sql.substring(s + MYBATIS_CONSTANT_VAR_START.length(), e).trim())
					+ sql.substring(e + MYBATIS_VAR_END.length());
			s = sql.indexOf(MYBATIS_CONSTANT_VAR_START);
		}
		logger.debug(sql);
		s = sql.indexOf(MYBATIS_MAPPING_VAR_START);
		while (s >= 0) {
			int e = sql.indexOf(MYBATIS_VAR_END, s);
			String columnName = sql.substring(s + MYBATIS_MAPPING_VAR_START.length(), e).trim();
			String columnType = "text";
			if (columnName.indexOf(",") > 0) {
				String[] columnNames = columnName.split("[,]");
				columnName = columnNames[0].trim();
				columnType = columnNames[1].trim();
			}
			if ("text".equals(columnType)) {
				sql = sql.substring(0, s) + "'"
						+ param.get(sql.substring(s + MYBATIS_MAPPING_VAR_START.length(), e).trim()) + "'"
						+ sql.substring(e + MYBATIS_VAR_END.length());
			} else {
				sql = sql.substring(0, s) + param.get(sql.substring(s + MYBATIS_MAPPING_VAR_START.length(), e).trim())
						+ sql.substring(e + MYBATIS_VAR_END.length());
			}
			s = sql.indexOf(MYBATIS_MAPPING_VAR_START);
		}
		logger.debug(sql);

		return sql;
	}

	class TableMetaFactor {
		String column;
		String type;
		boolean primaryKey;

		public TableMetaFactor(String column, String type, boolean pk) {
			this.column = column;
			this.type = type;
			this.primaryKey = pk;
		}

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public void setPrimaryKey(boolean primaryKey) {
			this.primaryKey = primaryKey;
		}
	}

	public class Response {
		public long size;
		public String message;
		public String request;
		public long latency_in_ms;
		public Map<String,String> queryTime;
		
		public Map<String,String> getQueryTime() {
			return queryTime;
		}

		public void setQueryTime(Map<String, String> queryTime) {
			this.queryTime = queryTime;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getRequest() {
			return request;
		}

		public void setRequest(String request) {
			this.request = request;
		}

		public long getLatency_in_ms() {
			return latency_in_ms;
		}

		public void setLatency_in_ms(long latency_in_ms) {
			this.latency_in_ms = latency_in_ms;
		}
	}

	
	public void calculateQueryTime(Map<String,Long> queryResponse) {

		String[][] stats = CassandraInstantPluginMap.getInstance().getQueryTime();
		if(stats == null)stats = new String[queryResponse.size()][5];
		
		for(String[] row : stats) {
			String sql = row[0];
			String max = row[1];
			String min = row[2];
			String avg = row[3];
			String count = row[4];
			if(sql == null || "".equals(sql)) {
				row[1] = queryResponse.get(sql) + "";
				row[2] = queryResponse.get(sql) + "";
				row[3] = queryResponse.get(sql) + "";
				row[4] = 1 +"";
			}else {
				row[1] = (Integer.parseInt(max) > queryResponse.get(sql)) ? max : queryResponse.get(sql) + "";
				row[2] = (Integer.parseInt(min) < queryResponse.get(sql)) ? min : queryResponse.get(sql) + "";
				row[3] = (Integer.parseInt(avg) + queryResponse.get(sql))/2  + "";
				row[4] = (Integer.parseInt(count) + 1)+"";
			}
		}
		logger.debug("====== SQL stats ========");
		logger.debug("" + queryResponse);
		logger.debug("====== SQL stats ========");
		CassandraInstantPluginMap.getInstance().setQueryTime(stats);
	}

}
