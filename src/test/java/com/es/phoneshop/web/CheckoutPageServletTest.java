package com.es.phoneshop.web;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    private CheckoutPageServlet servlet = new CheckoutPageServlet();
    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("order"), any());
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("First");
        when(request.getParameter("lastName")).thenReturn("Last");
        when(request.getParameter("paymentMethod")).thenReturn("CAHCE");
        when(request.getParameter("deliveryDate")).thenReturn("2031-12-03");
        when(request.getParameter("deliveryAddress")).thenReturn("111111");
        when(request.getParameter("phone")).thenReturn("+375297771122");

        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
        servlet.doPost(request, response);

        verify(request).getParameter("firstName");
        verify(request).getParameter("lastName");
        verify(request).getParameter("phone");
        verify(request).getParameter("deliveryDate");
        verify(request).getParameter("deliveryAddress");
        verify(request).getParameter("paymentMethod");
        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostWithErrors() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("");
        when(request.getParameter("lastName")).thenReturn("lastName");
        when(request.getParameter("paymentMethod")).thenReturn("CAHCE");
        when(request.getParameter("deliveryDate")).thenReturn("2");
        when(request.getParameter("deliveryAddress")).thenReturn("111111");
        when(request.getParameter("phone")).thenReturn("11111111");

        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
        servlet.doPost(request, response);

        verify(request).getParameter("firstName");
        verify(request).getParameter("lastName");
        verify(request).getParameter("phone");
        verify(request).getParameter("deliveryDate");
        verify(request).getParameter("deliveryAddress");
        verify(request).getParameter("paymentMethod");
        verify(requestDispatcher).forward(request, response);
    }
}
