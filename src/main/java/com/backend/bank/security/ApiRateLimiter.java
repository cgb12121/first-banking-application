package com.backend.bank.security;

import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ApiRateLimiter {

    private static final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 5;

    private static final long TIME_WINDOW = 60000; // 1min

    public boolean isRateLimited(String key) {
        long currentTime = System.currentTimeMillis();
        RequestCounter counter = requestCounts.computeIfAbsent(key, k -> new RequestCounter(MAX_REQUESTS, TIME_WINDOW));

        synchronized (counter) {
            if (counter.isRateLimited(currentTime)) {
                return true;
            }
            counter.addRequest(currentTime);
        }

        return false;
    }

    private static class RequestCounter {
        private final int maxRequests;
        private final long timeWindow;
        private int requestCount;
        private long windowStartTime;

        public RequestCounter(int maxRequests, long timeWindow) {
            this.maxRequests = maxRequests;
            this.timeWindow = timeWindow;
            this.windowStartTime = System.currentTimeMillis();
        }

        public boolean isRateLimited(long currentTime) {
            if (currentTime - windowStartTime > timeWindow) {
                requestCount = 0;  // Reset
                windowStartTime = currentTime;
            }
            return requestCount >= maxRequests;
        }

        public void addRequest(long currentTime) {
            if (currentTime - windowStartTime <= timeWindow) {
                requestCount++;
            }
        }
    }
}
