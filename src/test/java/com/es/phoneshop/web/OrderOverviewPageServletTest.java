package com.es.phoneshop.web;

import com.es.phoneshop.dao.order.MapOrderDao;
import com.es.phoneshop.dao.order.OrderDao;
import com.es.phoneshop.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderDao orderDao;
    @Mock
    private ServletConfig servletConfig;
    private OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();
    @Mock
    private ServletContext servletContext;
    @Before
    public void setup() throws ServletException {
        orderDao = MapOrderDao.getInstance();
        Order order = new Order();
        order.setSecureId("1");
        orderDao.save(order);
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test (expected = OrderNotFoundException.class)
    public void testDoGetWithIncorrectId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/2");
        when(servlet.getServletContext()).thenReturn(servletContext);
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(requestDispatcher).forward(request, response);
    }
}
