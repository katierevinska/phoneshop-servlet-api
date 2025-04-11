package com.es.phoneshop.security;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final DefaultDosProtectionService instance = new DefaultDosProtectionService();
    private final Map<String, RequestsNumberInfo> requestPerMinuteByAddressNumber = new ConcurrentHashMap<>();
    private static final Integer MAX_REQUESTS_PER_MINUTE = 20;

    private DefaultDosProtectionService() {
    }

    public static DefaultDosProtectionService getInstance() {
        return instance;
    }

    @Override
    public boolean isAllowed(String ip) {
        RequestsNumberInfo info = requestPerMinuteByAddressNumber.get(ip);
        if (info == null || Instant.now().getEpochSecond() - info.minuteStart.getEpochSecond() >= 60) {
            requestPerMinuteByAddressNumber.put(ip, new RequestsNumberInfo(1, Instant.now()));
            return true;
        }
        if (info.count >= MAX_REQUESTS_PER_MINUTE) {
            return false;
        }
        info.count += 1;
        return true;
    }

    @AllArgsConstructor
    private static class RequestsNumberInfo {
        public int count;
        public Instant minuteStart;
    }
}
