import java.util.*;

/**
 * Abstraction for generating unique short IDs.
 *
 * The service depends on this interface rather than a specific
 * ID-generation algorithm.
 *
 * This follows the principle:
 *
 * "Program to an interface, not an implementation."
 *
 * Current implementation:
 *
 * IdGenerator
 * │
 * ▼
 * CounterBasedGenerator
 * │
 * ▼
 * AtomicLong + Base62
 *
 * But we could later have:
 *
 * RandomIdGenerator
 * UUIDGenerator
 * SnowflakeIdGenerator
 * DatabaseSequenceGenerator
 *
 * without changing UrlShortenerService.
 */
interface IdGenerator {

    /**
     * Generates a new short identifier.
     *
     * @return a unique/generated ID as a String.
     */
    String generate();
}