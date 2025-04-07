package com.es.phoneshop.model.order;

import com.es.phoneshop.dao.product.MapProductDao;
import com.es.phoneshop.dao.product.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest
{
    private OrderService orderService;
    private CartService cartService;

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    Currency usd = Currency.getInstance("USD");

    private ProductDao productDao;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setup() {
        orderService = DefaultOrderService.getInstance();
        cartService = DefaultCartService.getInstance();
        productDao = MapProductDao.getInstance();
        ((MapProductDao)productDao).clear();
    }

    @Test
    public void testGetOrder() {
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        Cart cart = cartService.getCart(request);

        cartService.add(cart, 1L, 1);
        Order order = orderService.getOrder(cart);
        assertNotNull(order);
    }
}
