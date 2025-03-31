package com.es.phoneshop.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    private ProductListPageServlet servlet = new ProductListPageServlet();
    @Mock
    private ServletContext servletContext;
    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(servletContext.getInitParameter("insertDemoData")).thenReturn("true");

        DemoDataServletContextListener listener = new DemoDataServletContextListener();
        ServletContextEvent event = new ServletContextEvent(servletContext);
        listener.contextInitialized(event);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());

        verify(request).getParameter(eq("sort"));
        verify(request).getParameter(eq("order"));
        verify(request).getParameter(eq("query"));
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getParameter("productId")).thenReturn("1");;
        when(request.getLocale()).thenReturn(new Locale("en"));
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
        servlet.doPost(request, response);
        verify(request).getParameter(eq("quantity"));
        verify(request).getParameter(eq("productId"));
        verify(response).sendRedirect(any());

    }
}
