package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.product.MapProductDao;
import com.es.phoneshop.dao.product.ProductDao;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest
{
    private CartService cartService;

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    Currency usd = Currency.getInstance("USD");

    private ProductDao productDao;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setup() {
        cartService = DefaultCartService.getInstance();
        productDao = MapProductDao.getInstance();
        ((MapProductDao)productDao).clear();
    }

    @Test
    public void testGetCart() {
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        Cart cart = cartService.getCart(request);
        assertNotNull(cart);

        when(mockSession.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        Cart cartAfterGet = cartService.getCart(request);
        assertEquals(cart, cartAfterGet);
    }

    @Test
    public void testAdd() {
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        Cart cart = cartService.getCart(request);
        assertNotNull(cart);

        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        cartService.add(cart, 1L, 1);
        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 1);
        cartService.add(cart, 1L, 2);
        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 3);
    }

    @Test
    public void testUpdate() {
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        Cart cart = cartService.getCart(request);
        assertNotNull(cart);

        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        cartService.update(cart, 1L, 1);
        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 1);
        cartService.update(cart, 1L, 2);
        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 2);
    }

    @Test
    public void testDelete() {
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        Cart cart = cartService.getCart(request);
        assertNotNull(cart);

        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        cartService.update(cart, 1L, 1);
        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 1);
        cartService.delete(cart, 1L);
        assert(cart.getItems().isEmpty());
    }

    @Test
    public void testConcurrent() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        Cart cart = cartService.getCart(request);
        assertNotNull(cart);

        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        cartService.add(cart, 1L, 1);
        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 1);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    cartService.add(cart, 1L, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        assert(cart.getItems().size() == 1 && cart.getItems().get(0).getQuantity() == 1 + threadCount);
    }
}
