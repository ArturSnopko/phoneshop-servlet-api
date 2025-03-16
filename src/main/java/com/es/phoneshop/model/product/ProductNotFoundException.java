package com.es.phoneshop.model.product;

    public class ProductNotFoundException extends RuntimeException {
        private Long id;

        ProductNotFoundException(String message, Long id) {
            super(message);
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
