package org.example;

import org.example.config.RateLimitAlgorithm;
import org.example.config.RateLimitConfig;
import org.example.service.RateLimiterService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws InterruptedException {
        RateLimiterService service = new RateLimiterService();

        System.out.println("=== Example 1: Fixed Window (5 req / 10s) ===");
        service.configure(new RateLimitConfig("user_1", 5, 10_000, RateLimitAlgorithm.FIXED_WINDOW));
        for (int t : new int[]{0, 2, 4, 6, 8, 9, 10}) {
            boolean allowed = service.isAllowed("user_1", t * 1000L);
            System.out.printf("T=%2ds -> %s%n", t, allowed ? "Allowed" : "Rejected");
        }

        System.out.println("\n=== Example 2: Sliding Window (3 req / 5s) ===");
        service.configure(new RateLimitConfig("user_2", 3, 5_000, RateLimitAlgorithm.SLIDING_WINDOW));
        for (int t : new int[]{1, 2, 3, 4, 5, 6}) {
            boolean allowed = service.isAllowed("user_2", t * 1000L);
            System.out.printf("T=%2ds -> %s%n", t, allowed ? "Allowed" : "Rejected");
        }

        System.out.println("\n=== Token Bucket (10 req / 1s capacity, 12 requests fired at once) ===");
        service.configure(new RateLimitConfig("user_3", 10, 1_000, RateLimitAlgorithm.TOKEN_BUCKET));
        for (int i = 1; i <= 12; i++) {
            boolean allowed = service.isAllowed("user_3", 0L); // all at t=0 to show the burst cap
            System.out.printf("Request #%2d at T=0ms -> %s%n", i, allowed ? "Allowed" : "Rejected");
        }

        System.out.println("\n=== Concurrency proof: 200 threads, same instant, Leaky Bucket (100 req / 1s) ===");
        service.configure(new RateLimitConfig("user_4", 100, 1_000, RateLimitAlgorithm.LEAKY_BUCKET));
        int threadCount = 200;
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger allowedCount = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    if (service.isAllowed("user_4", 0L)) {
                        allowedCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        pool.shutdown();
        int allowed = allowedCount.get();
        System.out.println("Allowed " + allowed + " / " + threadCount + " (capacity was 100) -> " +
                (allowed == 100 ? "correct, no lost updates / no over-admits" : "RACE CONDITION BUG"));
    }
}
