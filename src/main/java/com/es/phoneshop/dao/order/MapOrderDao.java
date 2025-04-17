package com.es.phoneshop.dao.order;

import com.es.phoneshop.dao.AbstractMapDao;
import com.es.phoneshop.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;

import java.util.HashMap;
import java.util.Optional;


public class MapOrderDao extends AbstractMapDao<String, Order, OrderNotFoundException> implements OrderDao {
    private static class Holder {
        private static final MapOrderDao INSTANCE = new MapOrderDao();
    }

    public static MapOrderDao getInstance() {
        return Holder.INSTANCE;
    }

    private static final String ORDER_NOT_FOUND = "Order wasn't found, id:";

    private MapOrderDao() {
        super(new HashMap<>(),ORDER_NOT_FOUND);
    }

    @Override
    public Order getOrder(Long id) throws OrderNotFoundException {
        lock.readLock().lock();
        try {
            Optional<Order> res = dataMap.values().stream()
                    .filter(order -> id.equals(order.getId()))
                    .findFirst();
            if (res.isPresent()) {
                return res.get();
            }
            else {
                throw new OrderNotFoundException(ORDER_NOT_FOUND, id.toString());
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Order getOrderBySecureId(String id) throws OrderNotFoundException {
        return super.get(id);
    }

    @Override
    public void save(Order order) {
        lock.writeLock().lock();
        try{
            if (order.getId() == null) {
                order.setId(currentId.getAndAdd(1));
            } else {
                Optional<Order> res = dataMap.values().stream()
                        .filter(t -> t.getId().equals(order.getId()))
                        .findFirst();
                if (res.isPresent()) {
                    return;
                }
            }
            dataMap.put(order.getSecureId(), order);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(){
        super.clear();
    }

    @Override
    protected OrderNotFoundException getException(String mes, String key) throws OrderNotFoundException {
        return new OrderNotFoundException(mes,key);
    }
}
