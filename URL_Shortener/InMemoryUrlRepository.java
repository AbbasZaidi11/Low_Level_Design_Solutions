import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UrlRepository.
 *
 * Stores URL mappings inside a ConcurrentHashMap:
 *
 * shortCode ---> UrlMapping
 *
 * Example:
 *
 * "abc123" ---> UrlMapping(...)
 * "claude" ---> UrlMapping(...)
 *
 * Why ConcurrentHashMap?
 * The URL shortener may receive requests from multiple threads
 * simultaneously. ConcurrentHashMap allows safe concurrent access
 * without manually synchronizing every operation.
 *
 * Important operation:
 *
 * putIfAbsent(key, value)
 *
 * It atomically inserts the mapping ONLY if the short code
 * does not already exist.
 *
 * This prevents two concurrent requests from successfully claiming
 * the same short code.
 *
 * Note:
 * Since this repository is in-memory, all mappings disappear when
 * the application stops/restarts.
 *
 * A production system could replace this implementation with
 * MySQL, PostgreSQL, Redis, etc., while keeping the same
 * UrlRepository interface.
 */
class InMemoryUrlRepository implements UrlRepository {

    /*
     * Key = short code
     * Value = complete URL mapping
     *
     * ConcurrentHashMap is used instead of HashMap because this
     * repository may be accessed concurrently by multiple threads.
     */
    private final Map<String, UrlMapping> store = new ConcurrentHashMap<>();

    /**
     * Saves a mapping only if its short code is not already present.
     *
     * putIfAbsent() returns:
     *
     * null -> key didn't exist, new mapping was inserted
     * existing value -> key already existed, nothing was inserted
     *
     * Therefore:
     *
     * return true -> successfully inserted
     * return false -> short code already exists
     */
    public boolean saveIfAbsent(UrlMapping mapping) {
        return store.putIfAbsent(
                mapping.shortCode,
                mapping) == null;
    }

    /**
     * Finds a mapping using its short code.
     *
     * store.get(code) returns:
     *
     * UrlMapping -> if code exists
     * null -> if code doesn't exist
     *
     * Optional.ofNullable converts this into:
     *
     * Optional[UrlMapping]
     * Optional.empty()
     */
    public Optional<UrlMapping> findByShortCode(String code) {
        return
                Optional.ofNullable(store.get(code));
    }
}