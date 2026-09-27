import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents one URL mapping in the URL shortener.
 *
 * A UrlMapping connects:
 *
 * shortCode ---> longUrl
 *
 * It also stores:
 * - expiration time
 * - number of clicks
 *
 * Example:
 *
 * "abc123" ---> "https://example.com/some/very/long/url"
 * |
 * +-- expiresAt
 * +-- clicks
 *
 * This is a domain/model object.
 * It contains the data and small pieces of behavior directly
 * related to that data.
 */
class UrlMapping {

    // The short code users will use.
    // Example: https://short.ly/abc123
    final String shortCode;

    // The original URL to which the user should be redirected.
    final String longUrl;

    // Optional expiration time.
    // null means the URL never expires.
    final Instant expiresAt;

    /*
     * Number of times this short URL has been resolved/clicked.
     *
     * AtomicLong is used because multiple threads may resolve
     * the same URL simultaneously.
     *
     * Example:
     *
     * Thread A -> increment
     * Thread B -> increment
     *
     * AtomicLong ensures both increments happen safely.
     */
    final AtomicLong clicks = new AtomicLong();

    UrlMapping(String shortCode, String longUrl, Instant expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.expiresAt = expiresAt;
    }

    /**
     * Checks whether this URL has expired.
     *
     * If expiresAt == null:
     * URL never expires.
     *
     * Otherwise:
     * current time > expiration time
     * -> expired
     * current time <= expiration time
     * -> still valid
     */
    boolean isExpired() {
        return expiresAt != null
                && Instant.now().isAfter(expiresAt);
    }
}