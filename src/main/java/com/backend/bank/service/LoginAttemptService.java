package com.backend.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean isLoginAttemptAllowed(String identifier) {
        String key = buildKey(identifier);
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            return true;
        }

        Map<String, String> values = ops.entries(key);
        int attemptCount = values.containsKey("count") ? Integer.parseInt(values.get("count")) : 0;
        long lastAttemptTime = values.containsKey("timestamp") ? Long.parseLong(values.get("timestamp")) : 0L;

        long currentTime = System.currentTimeMillis();
        return attemptCount < 5 && (currentTime - lastAttemptTime) > TimeUnit.MINUTES.toMillis(5);
    }

    public void updateLoginAttempt(String identifier) {
        String key = buildKey(identifier);
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        ops.put(key, "count", String.valueOf(getLoginAttempts(key) + 1));
        ops.put(key, "timestamp", String.valueOf(System.currentTimeMillis()));
    }

    private String buildKey(String identifier) {
        return "login_attempts:" + (identifier);
    }

    private int getLoginAttempts(String key) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, String> values = ops.entries(key);
        return values.containsKey("count") ? Integer.parseInt(Objects.requireNonNull(values.get("count"))) : 0;
    }

}