package com.auto.test.jmeter.plugin.database.cassandra;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.database.AbstractSummerizer;
import com.auto.test.jmeter.plugin.database.AbstractSummerizer.AbstractRunningSampleWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.monitor.Monitors;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.CounterToRateMetricTransform;
import com.netflix.servo.publish.FileMetricObserver;
import com.netflix.servo.publish.MetricObserver;
import com.netflix.servo.publish.MonitorRegistryMetricPoller;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;

public class CassandraSummary extends AbstractSummerizer {

	static Logger logger = LoggerFactory.getLogger(CassandraSummary.class);

	public static boolean isStarted = false;
	
	private static boolean initalized = false;

	@Override
	protected void initializePlatform() {
		if (initalized)
			return;

		try {
			PollScheduler scheduler = PollScheduler.getInstance();
			scheduler.start();
			File logDir = new File("./logs/");
			if(!logDir.exists())logDir.mkdir();
			MetricObserver fileObserver = new FileMetricObserver("stats", logDir);
			MetricObserver transform = new CounterToRateMetricTransform(fileObserver, 2, TimeUnit.MINUTES);
			PollRunnable task = new PollRunnable(new MonitorRegistryMetricPoller(), BasicMetricFilter.MATCH_ALL,
					transform);
			scheduler.addPoller(task, 1, TimeUnit.MINUTES);
		} catch (Throwable e) {
			logger.error("Plugin was not intialized: ", e);
		}
		initalized = true;
	}

	@Override
	protected AbstractRunningSampleWrapper newRunningSampleWrapper(String label) {
		return new CassandraRunningSampleWrapper(label);
	}

	public static class CassandraRunningSampleWrapper extends AbstractRunningSampleWrapper {
		public final String name;

		public CassandraRunningSampleWrapper(String name) {
			super(name);
			this.name = ("JMeter_" + name).replace(" ", "_");
		}

		@Monitor(name = "ErrorPercentage", type = DataSourceType.GAUGE)
		public double getErrorPercentage() {
			return previous.getErrorPercentage();
		}

		@Monitor(name = "SampleCount", type = DataSourceType.GAUGE)
		public int getCount() {
			return previous.getCount();
		}

		@Monitor(name = "Rate", type = DataSourceType.GAUGE)
		public double getRate() {
			return previous.getRate();
		}

		@Monitor(name = "Mean", type = DataSourceType.GAUGE)
		public double getMean() {
			return previous.getMean();
		}

		@Monitor(name = "Min", type = DataSourceType.GAUGE)
		public long getMin() {
			return previous.getMin();
		}

		@Monitor(name = "Max", type = DataSourceType.GAUGE)
		public long getMax() {
			return previous.getMax();
		}

		@Monitor(name = "TotalBytes", type = DataSourceType.GAUGE)
		public long getTotalBytes() {
			return previous.getTotalBytes();
		}

		@Monitor(name = "StandardDeviation", type = DataSourceType.GAUGE)
		public double getStandardDeviation() {
			return previous.getStandardDeviation();
		}

		@Monitor(name = "AvgPageBytes", type = DataSourceType.GAUGE)
		public double getAvgPageBytes() {
			return previous.getAvgPageBytes();
		} 

		@Monitor(name = "BytesPerSecond", type = DataSourceType.GAUGE)
		public double getBytesPerSecond() {
			return previous.getBytesPerSecond();
		}

		@Monitor(name = "KBPerSecond", type = DataSourceType.GAUGE)
		public double getKBPerSecond() {
			return previous.getKBPerSecond();
		}

		@Override
		public void start() {
			Monitors.registerObject(name, this);
			isStarted = true;
		}

		@Override
		public void shutdown() {
			Monitors.unregisterObject(name, this);
			isStarted = false;
		}
	}
	
	private boolean existQueryInStats(Map<String,String> sql, String[][] stats) {
		boolean exist = false;
		for(String k : sql.keySet()) {
			exist = false;
			for(String[] row : stats) {
				if(k.equals(row[0])) {
					exist =true;
					break;
				}
			}
			if(!exist) return false;
		}
		return true;
	}

	@Override
	public void calculateQueryTime(SampleResult s) {
		ObjectMapper mapper = new ObjectMapper();
		Map object=null;
		try {
			logger.info("Cassandra Query Response : [{}]" , s.getResponseMessage());
			object = mapper.readValue(s.getResponseMessage(), Map.class);
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
		Map<String,String> queryResponse = (Map<String,String>)object;

		if(queryResponse.size() == 0) {
			logger.debug("##### Oops~~~~ queryResponse Time count is zero");
			return;
		}
		String[][] stats = CassandraInstantPluginMap.getInstance().getQueryTime();
		if(stats == null) logger.debug("queryResponseTime is null");
		else if(stats[0] == null) logger.debug("queryResponseTime[0] is null");
		else if(stats[0][0] == null) logger.debug("queryResponseTime[0][0] is null");
		else {
			try{logger.debug("first sql is : " + stats[0][0]);}catch(Exception e) {logger.debug("Exception : " + e.toString());}
			try{logger.debug("first max is : " + stats[0][1]);}catch(Exception e) {logger.debug("Exception : " + e.toString());}
			try{logger.debug("first min is : " + stats[0][2]);}catch(Exception e) {logger.debug("Exception : " + e.toString());}
			try{logger.debug("first avg is : " + stats[0][3]);}catch(Exception e) {logger.debug("Exception : " + e.toString());}
		}
		logger.debug("queryResponseTime --> " + queryResponse);
		
		if(stats == null || stats[0] == null || stats[0][0] == null || !existQueryInStats(queryResponse,stats)) {
			stats = new String[queryResponse.size()][5];
			int i = 0; 
			for(String k : queryResponse.keySet()) {
				stats[i][0] = k;
				stats[i][1] = queryResponse.get(k) + "";
				stats[i][2] = queryResponse.get(k) + "";
				stats[i][3] = queryResponse.get(k) + "";
				stats[i][4] = 1 +"";
				
				logger.debug("====== Query Stats One record ========");
				logger.debug("SQL IN MAP : " + k);
				logger.debug("VAL IN MAP : " + queryResponse.get(k));
				logger.debug("SQL : " + stats[i][0]);
				logger.debug("MAX : " + stats[i][1]);
				logger.debug("MIN : " + stats[i][2]);
				logger.debug("AVG : " + stats[i][3]);
				logger.debug("CNT : " + stats[i][4]);
				logger.debug("====== Query Stats One record ========");
				
				i++;
			}
		}else {
			for(String[] row : stats) {
				
				String sql = row[0];
				String max = row[1];
				String min = row[2];
				String avg = row[3];
				String count = row[4];
				
				int value = 0;
				try{
					value = Integer.parseInt(queryResponse.get(sql));
				}catch(Exception e) {
					logger.error(e.toString(),e);
					logger.debug("Number Format Exception["+sql+"] : " + queryResponse.get(sql));
					throw e;
				}
				 
				logger.debug("VALUE IN MAP ==> " + value);
				row[1] = (Integer.parseInt(max) > value) ? max : value + "";
				row[2] = (Integer.parseInt(min) < value) ? min : value + "";
				row[3] = (Integer.parseInt(avg) + value)/2  + "";
				row[4] = (Integer.parseInt(count) + 1)+"";
				
				logger.debug("====== Query Stats One record ========");
				logger.debug("SQL IN MAP : " + sql);
				logger.debug("VAL IN MAP : " + queryResponse.get(sql));
				logger.debug("SQL : " + row[0]);
				logger.debug("MAX : " + row[1]);
				logger.debug("MIN : " + row[2]);
				logger.debug("AVG : " + row[3]);
				logger.debug("CNT : " + row[4]);
				logger.debug("====== Query Stats One record ========");
			}
		}
		
		
		logger.debug("====== SQL stats ========");
		logger.debug("" + queryResponse);
		logger.debug("====== SQL stats ========");
		CassandraInstantPluginMap.getInstance().setQueryTime(stats);
	}

}
