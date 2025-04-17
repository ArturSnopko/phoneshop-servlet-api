package com.es.phoneshop.web;


import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.stream.IntStream;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private FilterConfig filterConfig;
    private DosFilter filter = new DosFilter();

    private static final int THRESHOLD = 20;

    @Before
    public void setup() throws ServletException {
        filter.init(filterConfig);
    }

    @Test
    public void testDoFilterAccept() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        filter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    public void testDoFilterRejects() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.2");
        IntStream.range(0, THRESHOLD).forEach(i -> {
            try {
                filter.doFilter(request, response, filterChain);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        filter.doFilter(request, response, filterChain);
        verify(response, times(1)).setStatus(429);
    }
}
