package com.restosync.comandas.service;

import com.restosync.comandas.exception.TooManyLoginAttemptsException;
import com.restosync.comandas.util.TextNormalizer;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 15;

    private final Clock clock = Clock.systemUTC();
    private final Map<String, AttemptBucket> attempts = new ConcurrentHashMap<>();

    public void assertNotBlocked(String email, String remoteAddress) {
        String key = key(email, remoteAddress);
        AttemptBucket bucket = attempts.get(key);
        if (bucket == null) return;

        if (bucket.expiresAt.isBefore(Instant.now(clock))) {
            attempts.remove(key);
            return;
        }

        if (bucket.count >= MAX_ATTEMPTS) {
            throw new TooManyLoginAttemptsException();
        }
    }

    public void recordFailure(String email, String remoteAddress) {
        attempts.compute(key(email, remoteAddress), (ignored, existing) -> {
            Instant now = Instant.now(clock);
            if (existing == null || existing.expiresAt.isBefore(now)) {
                return new AttemptBucket(1, now.plus(WINDOW_MINUTES, ChronoUnit.MINUTES));
            }
            return new AttemptBucket(existing.count + 1, existing.expiresAt);
        });
    }

    public void recordSuccess(String email, String remoteAddress) {
        attempts.remove(key(email, remoteAddress));
    }

    private String key(String email, String remoteAddress) {
        return (TextNormalizer.email(email) + "|" + remoteAddress).toLowerCase();
    }

    private record AttemptBucket(int count, Instant expiresAt) {
    }
}
