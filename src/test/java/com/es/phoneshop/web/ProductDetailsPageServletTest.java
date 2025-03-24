package com.es.phoneshop.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
    @Mock
    private ServletContext servletContext;
    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getInitParameter("insertDemoData")).thenReturn("true"); // Симуляция параметра

        DemoDataServletContextListener listener = new DemoDataServletContextListener();
        ServletContextEvent event = new ServletContextEvent(servletContext);
        listener.contextInitialized(event);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("product"), any());
        verify(request).getPathInfo();
    }

    @Test
    public void testDoPostWithoutErrors() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getLocale()).thenReturn(new Locale("en"));
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
        servlet.doPost(request, response);
        verify(request).getParameter(eq("quantity"));
        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostWithNotANumberError() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("asd");
        when(request.getLocale()).thenReturn(new Locale("en"));
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());

    }
    @Test
    public void testDoPostWithOutOfStockError() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("99999");
        when(request.getLocale()).thenReturn(new Locale("en"));
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);

        servlet.doPost(request, response);
        verify(request).setAttribute(eq("error"), any());

    }
}