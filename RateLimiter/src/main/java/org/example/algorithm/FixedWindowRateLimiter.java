package org.example.algorithm;


import org.example.config.RateLimitConfig;
import org.example.core.RateLimiter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Fixed (tumbling) window counter.
 *
 * A single counter is kept for the "current" window; once the window elapses
 * the counter resets to zero. O(1) time and space per user, which is why it's
 * the most common naive first answer — but it lets up to 2x maxRequests
 * through in a short burst that straddles a window boundary (e.g. limit 5/10s:
 * 5 requests at t=9.9s, window resets, 5 more at t=10.1s -> 10 requests in
 * 200ms). Mention this trade-off out loud in an interview; it's exactly the
 * gap SlidingWindowRateLimiter closes.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final ReentrantLock lock = new ReentrantLock();

    private long currentWindowStart = -1L;
    // Lazily initialized on first request
    private int requestCountInWindow;

    public FixedWindowRateLimiter(RateLimitConfig config){
        this.maxRequests = config.getMaxRequests();
        this.windowSizeMillis = config.getWindowSizeMillis();
    }

    @Override
    public boolean allowRequest(long timestampMillis) {
        lock.lock();
        try{
            if(currentWindowStart == -1L || timestampMillis - currentWindowStart >= windowSizeMillis){
                currentWindowStart = timestampMillis;
                requestCountInWindow = 0;
            }
            if(requestCountInWindow < maxRequests){
                requestCountInWindow++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
