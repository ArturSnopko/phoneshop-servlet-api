package com.es.phoneshop.model.product;

    public class ProductNotFoundException extends Exception {
        ProductNotFoundException(String message) {
            super(message);
        }
    }
