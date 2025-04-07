package com.es.phoneshop.dao;

import com.es.phoneshop.model.order.Order;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractMapDao <K, V, E extends Exception>{
    protected Map<K, V> dataMap;
    protected final AtomicLong currentId;
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected final String notFoundMessage;

    protected AbstractMapDao(Map<K, V> dataMap, String notFoundMessage) {
        this.dataMap = dataMap;
        currentId = new AtomicLong(1);
        this.notFoundMessage = notFoundMessage;
    }

    protected V get(K key) throws E {
        lock.readLock().lock();
        try {
            V res = dataMap.get(key);
            if (res == null)
                throw getException(notFoundMessage, key);
            return res;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear(){
        lock.writeLock().lock();
        try {
            dataMap.clear();
            currentId.set(1);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected abstract E getException (String mes, K key) throws E;
}
