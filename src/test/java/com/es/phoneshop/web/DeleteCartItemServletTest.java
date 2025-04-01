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
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    private DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
    }

    @Test
    public void testDoPost() throws IOException {
        when(request.getPathInfo()).thenReturn("/1");
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
        servlet.doPost(request, response);
        verify(response).sendRedirect(anyString());
        verify(request).getPathInfo();
    }
}
