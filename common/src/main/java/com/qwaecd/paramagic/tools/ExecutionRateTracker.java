package com.qwaecd.paramagic.tools;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class ExecutionRateTracker {
    private final AtomicLong counter = new AtomicLong(0);
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "rate-tracker");
                t.setDaemon(true);
                return t;
            });
    private final Deque<Long> recent = new ArrayDeque<>();
    private final int windowSizeSeconds; // 用于滑动平均
    private volatile long lastRate = 0;

    public ExecutionRateTracker(int windowSizeSeconds) {
        this.windowSizeSeconds = Math.max(1, windowSizeSeconds);
    }

    public void increment() {
        counter.incrementAndGet();
    }

    /** 获取最近一秒的执行次数（上次采样结果） */
    public long getLastRate() {
        return this.lastRate;
    }

    /** 获取最近 windowSizeSeconds 秒的滑动平均值 */
    public synchronized double getWindowAvg() {
        if (this.recent.isEmpty())
            return 0;
        long sum = 0;
        for (long v : this.recent)
            sum += v;
        return sum * 1.0 / this.recent.size();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::sample, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void sample() {
        long delta = counter.getAndSet(0);
        this.lastRate = delta;

        synchronized (this) {
            recent.addLast(delta);
            if (this.recent.size() > this.windowSizeSeconds) {
                this.recent.removeFirst();
            }
        }
        System.out.printf("Rate: %d ops/sec, windowAvg: %.2f ops/sec%n", delta, getWindowAvg());
    }
}
