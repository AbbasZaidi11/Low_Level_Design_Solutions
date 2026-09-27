package org.example.algorithm;

import org.example.config.RateLimitConfig;
import org.example.core.RateLimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sliding window log.
 *
 * Keeps the timestamp of every accepted request from the last windowSizeMillis.
 * On each new request, timestamps that have fallen out of the window are
 * evicted from the front of the deque (they're in insertion order, so this is
 * amortized O(1) per eviction), then the remaining count decides allow/reject.
 *
 * Accurate — no boundary-burst issue like fixed window — at the cost of
 * O(maxRequests) memory per user instead of O(1). For high-limit users
 * (e.g. 10,000 req/min) a sliding window *counter* (weighted average of the
 * previous and current fixed windows) is the usual production compromise;
 * worth naming that alternative even if you don't implement it.
 */

public class SlidingWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final Deque<Long> requestTimestamps  = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    public SlidingWindowRateLimiter(RateLimitConfig config){
        this.maxRequests = config.getMaxRequests();
        this.windowSizeMillis = config.getWindowSizeMillis();
    }

    @Override
    public boolean allowRequest(long timestampsMillis){
        lock.lock();
        try{
            long windowStart = timestampsMillis - windowSizeMillis;
            while(!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() <= windowStart){
                requestTimestamps.pollFirst();
            }
            if(requestTimestamps.size() < maxRequests){
                requestTimestamps.addLast(timestampsMillis);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
