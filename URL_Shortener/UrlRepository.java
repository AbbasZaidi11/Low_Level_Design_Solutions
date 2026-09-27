import java.util.Optional;

/**
 * Repository abstraction for storing and retrieving URL mappings.
 *
 * The service layer talks to this interface instead of directly
 * interacting with a HashMap, database, Redis, etc.
 *
 * This gives us separation of concerns:
 *
 * UrlShortenerService
 * |
 * v
 * UrlRepository <-- abstraction
 * |
 * ├── InMemoryUrlRepository
 * ├── MySqlUrlRepository
 * └── RedisUrlRepository
 *
 * The implementation can change without changing the service logic.
 */
interface UrlRepository {

    /**
     * Saves a URL mapping only if its short code is not already present.
     *
     * @param mapping URL mapping to store.
     * @return true if saved successfully,
     *         false if the short code already exists.
     *
     *         Important:
     *         The check + insert should ideally be atomic in a real
     *         concurrent implementation to prevent race conditions.
     */
    boolean saveIfAbsent(UrlMapping mapping);

    /**
     * Finds a URL mapping using its short code.
     *
     * Optional is used because the mapping may not exist.
     *
     * @param code Short URL code.
     * @return Optional containing the mapping if found,
     *         otherwise Optional.empty().
     */
    Optional<UrlMapping> findByShortCode(String code);
}