package org.example.framework.aop.profile;

import java.util.concurrent.atomic.AtomicLong;

public class MethodProfile {

    private final AtomicLong count = new AtomicLong();
    private final AtomicLong totalTimeNs = new AtomicLong();

    public void record(long elapsedNs) {
        count.incrementAndGet();
        totalTimeNs.addAndGet(elapsedNs);
    }

    public long getCount() {
        return count.get();
    }

    public long getTotalTimeNs() {
        return totalTimeNs.get();
    }

    public long getAverageTimeNs() {
        long c = count.get();
        return c == 0 ? 0 : totalTimeNs.get() / c;
    }
}