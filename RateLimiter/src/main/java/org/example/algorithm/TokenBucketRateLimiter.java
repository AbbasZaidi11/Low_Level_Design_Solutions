package org.example.algorithm;

import org.example.config.RateLimitConfig;
import org.example.core.RateLimiter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Token bucket.
 *
 * The bucket holds up to `capacity` tokens (= maxRequests) and refills
 * continuously at capacity / windowSizeMillis tokens per millisecond. Every
 * request consumes one token; if none are available, reject. Refill is
 * computed lazily from elapsed time on each call rather than via a
 * background thread — no timer, no wasted wakeups, same result.
 *
 * This is what most real APIs (Stripe, AWS, GitHub) actually expose: it
 * allows a burst up to full capacity (nice for bursty legitimate clients)
 * while still enforcing the long-run average rate.
 */
public class TokenBucketRateLimiter implements RateLimiter {
    private final double capacity;
    private final double refillTokensPerMillis;
    private final ReentrantLock lock = new ReentrantLock();

    private double availableTokens;
    private long lastRefillTimestamp = -1L;

    public TokenBucketRateLimiter(RateLimitConfig config) {
        this.capacity = config.getMaxRequests();
        this.refillTokensPerMillis = (double) config.getMaxRequests() / config.getWindowSizeMillis();
        this.availableTokens = capacity; // start full, like a fresh bucket
    }

    @Override
    public boolean allowRequest(long timestampMillis) {
        lock.lock();
        try {
            refill(timestampMillis);
            if (availableTokens >= 1.0) {
                availableTokens -= 1.0;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refill(long timestampMillis) {
        if (lastRefillTimestamp == -1L) {
            lastRefillTimestamp = timestampMillis;
            return;
        }
        long elapsed = timestampMillis - lastRefillTimestamp;
        if (elapsed <= 0) return;
        double tokensToAdd = elapsed * refillTokensPerMillis;
        availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
        lastRefillTimestamp = timestampMillis;
    }

}
