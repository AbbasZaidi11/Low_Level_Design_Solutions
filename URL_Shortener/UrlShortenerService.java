import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * Business/service layer for the URL shortener.
 *
 * Responsibilities:
 * 1. Validate incoming URLs.
 * 2. Generate a short code when the user doesn't provide an alias.
 * 3. Create and persist the URL mapping.
 * 4. Resolve a short code back to the original URL.
 * 5. Check whether a mapping has expired.
 * 6. Track the number of clicks.
 *
 * This class does NOT handle storage directly.
 * It delegates persistence to UrlRepository and ID generation to IdGenerator.
 *
 * Main flow:
 *
 * shorten():
 * Long URL -> validate -> generate/use code -> save -> return code
 *
 * resolve():
 * Short code -> lookup -> check expiry -> increment clicks -> return long URL
 */
class UrlShortenerService {

    // Handles persistence/retrieval of URL mappings.
    private final UrlRepository repository;

    // Generates unique short codes when no custom alias is provided.
    private final IdGenerator generator;

    UrlShortenerService(UrlRepository repository, IdGenerator generator) {
        this.repository = repository;
        this.generator = generator;
    }

    /**
     * Creates a shortened URL.
     *
     * @param longUrl   Original URL.
     * @param alias     Optional custom short code.
     * @param expiresAt Optional expiration time.
     * @return The generated/custom short code.
     */
    String shorten(String longUrl, String alias, Instant expiresAt) {

        // Make sure the supplied URL is syntactically valid.
        validate(longUrl);

        // Use the user's alias if provided; otherwise generate a new ID.
        String code = (alias != null)
                ? alias
                : generator.generate();

        // Create the object that represents the short -> long URL mapping.
        UrlMapping mapping = new UrlMapping(code, longUrl, expiresAt);

        /*
         * Save only if the short code is not already being used.
         *
         * This operation should ideally be atomic inside the repository
         * to prevent two concurrent requests from using the same code.
         */
        if (!repository.saveIfAbsent(mapping)) {
            throw new IllegalStateException(
                    "Code already in use: " + code);
        }

        return code;
    }

    /**
     * Resolves a short code into its original URL.
     *
     * @param code Short URL code.
     * @return Original long URL.
     */
    String resolve(String code) {

        // Look up the mapping. Throw if the code doesn't exist.
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new NoSuchElementException(
                        "Not found: " + code));

        // Don't redirect to an expired URL.
        if (mapping.isExpired()) {
            throw new NoSuchElementException(
                    "Expired: " + code);
        }

        // Track this redirect. AtomicLong makes concurrent increments safe.
        mapping.clicks.incrementAndGet();

        // Return the original URL so the caller can redirect the user.
        return mapping.longUrl;
    }

    /**
     * Validates that the supplied string represents a usable URI
     * containing both a scheme and a host.
     *
     * Examples of valid URLs:
     * https://google.com
     * https://example.com/path
     *
     * Examples rejected:
     * "hello"
     * "google.com"
     * malformed URLs
     */
    private void validate(String url) {
        try {

            URI uri = new URI(url);

            // A URL should have something like "https" as its scheme
            // and "google.com" as its host.
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException(
                        "Bad URL: " + url);
            }

        } catch (URISyntaxException e) {

            // Convert the checked URI parsing exception into a
            // simpler application-level IllegalArgumentException.
            throw new IllegalArgumentException(
                    "Bad URL: " + url);
        }
    }
}