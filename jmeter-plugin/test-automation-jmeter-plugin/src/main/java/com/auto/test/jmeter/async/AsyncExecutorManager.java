package com.auto.test.jmeter.async;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.test.jmeter.plugin.common.gui.TestAutomationGuiController;
import com.netflix.jmeter.utils.SystemUtils;


public class AsyncExecutorManager {
	
	static Logger logger = LoggerFactory.getLogger(AsyncExecutorManager.class);
	
	ExecutorService threadPoolExecutor;
	
	int minThreadPoolCount = 10;
	int maxThreadPoolCount = 20;
	int keepAliveTime = 10000;
	
	static AsyncExecutorManager manager;
	
	static {
		manager = new AsyncExecutorManager();
	}
	
	public AsyncExecutorManager() {
		this.init();
	}
	
	public static AsyncExecutorManager getINSTANCE() {
		if(manager == null) {
			manager = new AsyncExecutorManager();
		}
		return manager;
	}
	
	
	public void init() {
		threadPoolExecutor = new ThreadPoolExecutor(this.minThreadPoolCount, this.maxThreadPoolCount,
				this.keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public void init(int minThreadPoolCount, int maxThreadPoolCount, int keepAliveTime ) {
		this.minThreadPoolCount = minThreadPoolCount;
		this.maxThreadPoolCount = maxThreadPoolCount;
		this.keepAliveTime = keepAliveTime;
		this.init();
	}
	
	public Future<?> callThread(Callable<Map<String,Object>> runnable) throws Exception {
		return threadPoolExecutor.submit(runnable);
	}
	
	public Future executeThread(Runnable runnable) throws Exception {
		return threadPoolExecutor.submit(runnable);
	}
	
	public void shutdown() {
		threadPoolExecutor.shutdown();
	}
}
