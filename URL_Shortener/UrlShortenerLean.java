/**
 * Simple demo / entry point for the URL shortener.
 *
 * This class wires together the application's dependencies and
 * demonstrates the main use cases:
 *
 * 1. Generate a short URL automatically.
 * 2. Resolve a short URL back to the original URL.
 * 3. Create a custom alias.
 * 4. Detect duplicate aliases.
 * 5. Reject invalid URLs.
 *
 * Architecture:
 *
 * UrlShortenerLean
 * |
 * v
 * UrlShortenerService
 * / \
 * v v
 * UrlRepository IdGenerator
 * | |
 * v v
 * InMemory... CounterBased...
 *
 * This is essentially a small integration/demo test of the
 * components built for the URL shortener.
 */
public class UrlShortenerLean {

    public static void main(String[] args) {

        // Use an in-memory repository instead of a real database.
        // Useful for demos, learning and testing.
        UrlRepository repo = new InMemoryUrlRepository();

        // Inject the repository and ID generator into the service.
        UrlShortenerService service = new UrlShortenerService(
                repo,
                new CounterBasedGenerator());

        // ---------------------------------------------------------
        // 1. Normal URL shortening
        // ---------------------------------------------------------

        // alias = null means the service will generate the short code.
        String code = service.shorten(
                "https://www.example.com/very/long/path",
                null,
                null);

        // Resolve the generated code back to the original URL.
        System.out.println(
                "shortened: " + code + " -> " + service.resolve(code));

        // ---------------------------------------------------------
        // 2. Custom alias
        // ---------------------------------------------------------

        // Instead of generating a code, explicitly use "claude".
        String alias = service.shorten(
                "https://www.anthropic.com",
                "claude",
                null);

        System.out.println("custom alias: " + alias);

        // ---------------------------------------------------------
        // 3. Duplicate alias / conflict
        // ---------------------------------------------------------

        // "claude" already exists, so saveIfAbsent() should fail.
        try {
            service.shorten(
                    "https://www.google.com",
                    "claude",
                    null);
        } catch (IllegalStateException e) {

            // Expected behavior: duplicate short code is rejected.
            System.out.println(
                    "conflict caught: " + e.getMessage());
        }

        // ---------------------------------------------------------
        // 4. Invalid URL
        // ---------------------------------------------------------

        try {
            // This should fail validation inside the service.
            service.shorten(
                    "not-a-url",
                    null,
                    null);
        } catch (IllegalArgumentException e) {

            // Expected behavior: malformed URL is rejected.
            System.out.println(
                    "bad url caught: " + e.getMessage());
        }
    }
}