package com.es.phoneshop.security;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private static final long MIN_TIME_BETWEEN_CLEARS = 60000 * 60;
    private long lastClearTime = System.currentTimeMillis();
    private final Map<String, Queue<Long>> requestTimes = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String ip){
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastClearTime > MIN_TIME_BETWEEN_CLEARS) {
            Iterator<Map.Entry<String, Queue<Long>>> iterator = requestTimes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Queue<Long>> entry = iterator.next();
                Queue<Long> times = entry.getValue();
                synchronized (times) {
                    while (!times.isEmpty() && currentTime - times.peek() > TIME_LIMIT) {
                        times.poll();
                    }
                }
                if (times.isEmpty()) {
                    iterator.remove();
                }
            }
            lastClearTime = currentTime;
        }

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
