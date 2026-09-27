package org.example.config;

import java.util.Objects;

public final class RateLimitConfig {

    private final String userId;
    private final int maxRequests;
    private final long windowSizeMillis;
    private final RateLimitAlgorithm algorithm;

    public RateLimitConfig(String userId, int maxRequests, long windowSizeMillis,
                           RateLimitAlgorithm algorithm) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowSizeMillis <= 0) {
            throw new IllegalArgumentException("windowSizeMillis must be positive");
        }
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm cannot be null");
    }

    public String getUserId() { return userId; }
    public int getMaxRequests() { return maxRequests; }
    public long getWindowSizeMillis() { return windowSizeMillis; }
    public RateLimitAlgorithm getAlgorithm() { return algorithm; }
}
