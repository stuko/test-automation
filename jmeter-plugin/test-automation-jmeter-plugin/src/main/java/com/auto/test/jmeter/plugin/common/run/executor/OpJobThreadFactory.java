package com.auto.test.jmeter.plugin.common.run.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class OpJobThreadFactory implements ThreadFactory {
    private int priority;
    private boolean daemon;
    private final String namePrefix;
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public OpJobThreadFactory(int priority) {
        this(priority, true);
    }

    public OpJobThreadFactory(int priority, boolean daemon) {
        this.priority = priority;
        this.daemon = daemon;
        namePrefix = "jobpool-" +poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        t.setPriority(priority);
        return t;
    }
}
