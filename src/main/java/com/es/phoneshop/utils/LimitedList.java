package com.es.phoneshop.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LimitedList <T> implements Serializable {
    private final List<T> items;
    private final int limit;

    public LimitedList(int limit) {
        this.limit = limit;
        this.items = new ArrayList<T>();
    }

    public int getLimit() {
        return limit;
    }

    public List<T> getItems() {
        return items;
    }

    public void addItem(T item) {
        if (items.size() == limit) {
            items.remove(0);
        }
        items.add(item);
    }

    public boolean removeItem(T item) {
        return items.remove(item);
    }

    @Override
    public String toString() {
        return items.toString();
    }

}
