package com.es.phoneshop.exceptions;

public class OrderNotFoundException extends RuntimeException {
    private final String id;

    public OrderNotFoundException(String message, String id) {
        super(message);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
