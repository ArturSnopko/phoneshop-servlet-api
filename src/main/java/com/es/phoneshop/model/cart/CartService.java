package com.es.phoneshop.model.cart;

import com.es.phoneshop.exceptions.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CartService {
    Cart getCart(HttpServletRequest request);
    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;
    void update(Cart cart, Long productId, int quantity) throws OutOfStockException;
    void delete(Cart cart, Long productId) ;

    List<String> getSearchOptions();
}
