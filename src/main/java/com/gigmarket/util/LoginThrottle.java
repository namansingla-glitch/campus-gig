package com.gigmarket.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very small in-memory brute-force guard: after MAX_FAILURES wrong passwords for the same
 * email + IP within WINDOW_MS, further attempts are refused until the window passes.
 */
public final class LoginThrottle {

    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_MS = 10 * 60 * 1000L;
    private static final Map<String, Deque<Long>> FAILURES = new ConcurrentHashMap<>();

    private LoginThrottle() {
    }

    public static boolean isBlocked(String key) {
        Deque<Long> times = FAILURES.get(key);
        if (times == null) {
            return false;
        }
        synchronized (times) {
            prune(times);
            if (times.isEmpty()) {
                FAILURES.remove(key);
                return false;
            }
            return times.size() >= MAX_FAILURES;
        }
    }

    public static void recordFailure(String key) {
        Deque<Long> times = FAILURES.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (times) {
            prune(times);
            times.addLast(System.currentTimeMillis());
        }
    }

    public static void reset(String key) {
        FAILURES.remove(key);
    }

    private static void prune(Deque<Long> times) {
        long cutoff = System.currentTimeMillis() - WINDOW_MS;
        Iterator<Long> it = times.iterator();
        while (it.hasNext()) {
            if (it.next() < cutoff) {
                it.remove();
            } else {
                break;
            }
        }
    }
}
