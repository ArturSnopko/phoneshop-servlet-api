package com.es.phoneshop.security;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService{
    private static class Holder {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }
    public static DefaultDosProtectionService getInstance() {
        return DefaultDosProtectionService.Holder.INSTANCE;
    }

    private DefaultDosProtectionService(){}

    private static final long THRESHOLD = 20;
    private static final long TIME_LIMIT = 60000;
    private final Map<String, Queue<Long>> requestTimes = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String ip){
        long currentTime = System.currentTimeMillis();
        Queue<Long> timestamps = requestTimes.computeIfAbsent(ip, k -> new LinkedList<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && currentTime - timestamps.peek() > TIME_LIMIT) {
                timestamps.poll();
            }

            if (timestamps.size() >= THRESHOLD) {
                return false;
            }
            timestamps.add(currentTime);
            return true;
        }
    }
}
