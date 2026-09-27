package org.example.algorithm;

import org.example.config.RateLimitConfig;
import org.example.core.RateLimiter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Leaky bucket (virtual scheduling variant).
 *
 * A textbook leaky bucket runs an actual worker thread that pulls requests
 * off a queue at a fixed rate. That needs a queue per user plus a live thread
 * (or shared scheduler) per user — expensive at scale and awkward for a
 * synchronous allow/reject API. Instead we track the bucket's current "water
 * level" as a number and lazily "leak" it based on elapsed time on every
 * call — same admission behavior, no background thread. This is the leaky
 * bucket you'll actually see in a machine-coding round; mention the
 * real-queue version exists so the interviewer knows it's a deliberate choice.
 *
 * Net effect vs. token bucket: token bucket allows a burst up to full
 * capacity immediately; leaky bucket smooths output to a near-constant rate
 * even if requests arrive in a burst.
 */
public class LeakyBucketRateLimiter implements RateLimiter {

    private final double capacity;
    private final double leakRatePerMillis;
    private final ReentrantLock lock = new ReentrantLock();

    private double currentLevel = 0.0;
    private long lastLeakTimestamp = -1L;

    public LeakyBucketRateLimiter(RateLimitConfig config) {
        this.capacity = config.getMaxRequests();
        this.leakRatePerMillis = (double) config.getMaxRequests() / config.getWindowSizeMillis();
    }

    @Override
    public boolean allowRequest(long timestampMillis) {
        lock.lock();
        try {
            leak(timestampMillis);
            if (currentLevel + 1.0 <= capacity) {
                currentLevel += 1.0;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void leak(long timestampMillis) {
        if (lastLeakTimestamp == -1L) {
            lastLeakTimestamp = timestampMillis;
            return;
        }
        long elapsed = timestampMillis - lastLeakTimestamp;
        if (elapsed <= 0) return;
        double leaked = elapsed * leakRatePerMillis;
        currentLevel = Math.max(0.0, currentLevel - leaked);
        lastLeakTimestamp = timestampMillis;
    }
}
