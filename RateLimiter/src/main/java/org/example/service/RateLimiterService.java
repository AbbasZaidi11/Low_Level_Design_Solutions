package org.example.service;

import org.example.config.RateLimitConfig;
import org.example.core.RateLimiter;
import org.example.factory.RateLimiterFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Facade the rest of the application talks to (this is what an API Gateway
 * filter / interceptor would call on every incoming request). Owns:
 *   - userId -> RateLimitConfig   (so tiers / limits can be looked up or changed)
 *   - userId -> live RateLimiter  (so algorithm state persists across calls)
 *
 * Thread-safety: both maps are ConcurrentHashMap, and limiter creation goes
 * through computeIfAbsent, which is atomic per key. That matters here because
 * without it, two threads racing on the very first request from a brand-new
 * user could each construct their own FixedWindowRateLimiter, and one would
 * silently overwrite the other's counted request — a real bug in a naive
 * "if (map.get(id) == null) map.put(id, new X())" implementation.
 */
public class RateLimiterService {

    private final ConcurrentHashMap<String, RateLimitConfig> userConfigs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateLimiter> userLimiters = new ConcurrentHashMap<>();

    /** Registers or updates a user's rate limit configuration (e.g. tier upgrade). */
    public void configure(RateLimitConfig config) {
        userConfigs.put(config.getUserId(), config);
        // A config change (tier upgrade, algorithm switch) should take effect
        // immediately rather than being silently ignored by an
        // already-constructed limiter instance still holding the old rules.
        userLimiters.put(config.getUserId(), RateLimiterFactory.create(config));
    }

    /**
     * @return true if allowed, false if it should be rejected
     * @throws UserNotRegisteredException if no config exists for this user
     */
    public boolean isAllowed(String userId, long timestampMillis) {
        RateLimiter limiter = userLimiters.computeIfAbsent(userId, id -> {
            RateLimitConfig config = userConfigs.get(id);
            if (config == null) {
                throw new UserNotRegisteredException(id);
            }
            return RateLimiterFactory.create(config);
        });
        return limiter.allowRequest(timestampMillis);
    }

    /** Convenience overload for real (non-simulated) traffic. */
    public boolean isAllowed(String userId) {
        return isAllowed(userId, System.currentTimeMillis());
    }

    /** Throws instead of returning false — convenient at an actual filter/interceptor boundary. */
    public void checkAndThrow(String userId, long timestampMillis) {
        if (!isAllowed(userId, timestampMillis)) {
            throw new RateLimitExceededException(userId);
        }
    }
}
