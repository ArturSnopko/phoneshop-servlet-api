package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.MapProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;

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
        lock.readLock().lock();
        Cart cart;
        try {
            cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
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
            CartItem currentItem = cart.getItems().stream()
                    .filter(item -> productId.equals(item.getProduct().getId()))
                    .findAny()
                    .orElse(null);

            Product product = productDao.getProduct(productId);

            int overallQuantity = currentItem == null ? quantity: currentItem.getQuantity() + quantity;

            if (product.getStock() < overallQuantity) {
                throw new OutOfStockException(product, quantity, product.getStock());
            }
            if (currentItem == null) {
                cart.getItems().add(new CartItem(product, quantity));
            } else {
                currentItem.setQuantity(overallQuantity);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
