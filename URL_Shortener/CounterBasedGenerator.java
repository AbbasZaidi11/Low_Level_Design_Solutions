import java.util.concurrent.atomic.AtomicLong;

/**
 * Counter-based implementation of IdGenerator.
 *
 * Generates IDs by:
 *
 * 1. Atomically incrementing a counter.
 * 2. Converting the number from decimal to Base62.
 *
 * Example:
 *
 * counter value: 62
 * ↓
 * Base62 conversion
 * ↓
 * "10"
 *
 * Base62 alphabet:
 *
 * 0-9 = 0-9
 * A-Z = 10-35
 * a-z = 36-61
 *
 * Why AtomicLong?
 *
 * Multiple threads may call generate() simultaneously.
 * AtomicLong guarantees that each thread gets a different
 * counter value.
 *
 * Why Base62?
 *
 * It makes the numeric ID much shorter.
 *
 * Decimal: 123456
 * Base62: "w7E"
 *
 * Important limitation:
 *
 * This generator is only unique within this particular generator
 * instance. If multiple servers each start their own counter at 0,
 * they can generate duplicate IDs.
 *
 * A distributed system would need something like:
 * - database sequence
 * - Redis/incrementing service
 * - UUID
 * - Snowflake-style IDs
 * - or a node/server ID combined with the counter
 */
class CounterBasedGenerator implements IdGenerator {

    // 62 possible characters for each Base62 digit.
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // Thread-safe counter shared by all calls to generate().
    private final AtomicLong counter = new AtomicLong();

    /**
     * Generates the next short ID.
     */
    public String generate() {

        // Atomically increment the counter and get the new value.
        long value = counter.incrementAndGet();

        StringBuilder sb = new StringBuilder();

        /*
         * Convert the decimal number into Base62.
         *
         * value % 62
         * -> gives the current Base62 digit
         *
         * value / 62
         * -> moves to the next digit
         */
        while (value > 0) {

            // Convert remainder (0-61) into a character.
            sb.append(
                    ALPHABET.charAt((int) (value % 62)));

            // Remove the digit we just processed.
            value /= 62;
        }

        /*
         * Digits are generated from right to left,
         * so reverse them to get the correct order.
         */
        return sb.reverse().toString();
    }
}