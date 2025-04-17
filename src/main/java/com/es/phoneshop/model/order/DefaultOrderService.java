package com.es.phoneshop.model.order;

import com.es.phoneshop.dao.order.MapOrderDao;
import com.es.phoneshop.dao.order.OrderDao;
import com.es.phoneshop.enums.PaymentMethod;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    private static class Holder {
        private static final DefaultOrderService INSTANCE = new DefaultOrderService();
    }

    public static DefaultOrderService getInstance() {
        return Holder.INSTANCE;
    }

    private DefaultOrderService() {

    }

    private OrderDao orderDao = MapOrderDao.getInstance();
    private static final int DELIVERY_COST = 5;

    @Override
    public Order getOrder(Cart cart) {
        Order order = new Order();

        order.setItems(cart.getItems().stream().map(item -> {
            try {
                return (CartItem)item.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).toList());

        order.setSubtotal(cart.getTotalCost() == null ? new BigDecimal(0) : cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));

        return order;
    }

    private BigDecimal calculateDeliveryCost(){
        return new BigDecimal(DELIVERY_COST);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(){
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
    }

}
