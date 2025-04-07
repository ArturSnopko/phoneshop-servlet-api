package com.es.phoneshop.dao.order;

import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


public class MapOrderDaoTest
{
    private OrderDao orderDao;

    @Before
    public void setup() {
        orderDao = MapOrderDao.getInstance();
        ((MapOrderDao)orderDao).clear();
    }

    @Test
    public void testGetOrderById(){
        Order order = new Order();
        order.setId(4L);
        orderDao.save(order);
        Order result = orderDao.getOrder(4L);
        assertNotNull(result);
    }

    @Test
    public void testGetOrderBySecureId(){
        Order order = new Order();
        String secureId =  UUID.randomUUID().toString();
        order.setSecureId(secureId);
        orderDao.save(order);
        Order result = orderDao.getOrderBySecureId(secureId);
        assertNotNull(result);
    }

    @Test
    public void testConcurrent() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long finalI = i;
            executorService.execute(() -> {
                try {
                    Order order = new Order();
                    order.setId(finalI);
                    order.setSecureId(UUID.randomUUID().toString());
                    orderDao.save(order);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        for (int i = 0; i < threadCount; i++) {
            Order order = orderDao.getOrder((long)i);
            assertNotNull(order);
        }
    }
}
