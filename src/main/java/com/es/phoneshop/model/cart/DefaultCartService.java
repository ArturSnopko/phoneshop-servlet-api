package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.product.MapProductDao;
import com.es.phoneshop.dao.product.ProductDao;
import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {
    private static class Holder {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return Holder.INSTANCE;
    }

    private DefaultCartService() {
        productDao = MapProductDao.getInstance();
    }

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ProductDao productDao;

    @Override
    public Cart getCart(HttpServletRequest request) {
        lock.writeLock().lock();
        Cart cart;
        try {
            cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
            }
            return cart;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        try{
          addIntoCart(cart, productId, quantity, false);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        try{
            addIntoCart(cart, productId, quantity, true);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(Cart cart, Long productId) {
        lock.writeLock().lock();
        try {
            cart.getItems().removeIf(item -> productId.equals(item.getProduct().getId()));
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .mapToInt(q -> q)
                .sum());
        cart.setTotalCost(cart.getItems().stream()
                .map(item -> item.getProduct().
                        getPrice().
                        multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    private void addIntoCart(Cart cart, Long productId, int quantity, boolean update) {
        CartItem currentItem = cart.getItems().stream()
                .filter(item -> productId.equals(item.getProduct().getId()))
                .findAny()
                .orElse(null);

        Product product = productDao.getProduct(productId);
        int overallQuantity;

        if (update) {
            overallQuantity = quantity;
        } else {
            overallQuantity = currentItem == null ? quantity: currentItem.getQuantity() + quantity;
        }

        if (product.getStock() < overallQuantity || quantity <= 0) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
        if (currentItem == null) {
            cart.getItems().add(new CartItem(product, quantity));
        } else {
            currentItem.setQuantity(overallQuantity);
        }

        recalculateCart(cart);
    }
}
