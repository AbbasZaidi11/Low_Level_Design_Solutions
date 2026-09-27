package org.example.core;

/**
 * Strategy interface every rate-limiting algorithm implements.
 * The timestamp is passed in explicitly (instead of each implementation
 * calling System.currentTimeMillis() internally) so that:
 *   1. The algorithms are deterministic and trivially unit-testable.
 *   2. RateLimiterService is the single place that decides what "now" means.
 */
public interface RateLimiter {

    /**
     * @param timestampMillis time at which the request arrives (epoch millis)
     * @return true if the request should be allowed, false if it should be rejected
     */
    boolean allowRequest(long timestampMillis);
}
