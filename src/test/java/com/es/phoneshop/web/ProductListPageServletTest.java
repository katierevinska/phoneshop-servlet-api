package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;

    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @BeforeEach
    public void setup() throws ServletException {
        servlet.init();
        servlet.setProductDao(productDao);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetProcess() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());
    }

    @Test
    void testDoGetNotNullFindProductsParameters() throws ServletException, IOException {
        when(request.getParameter("query")).thenReturn("phone1");
        when(request.getParameter("sort")).thenReturn("price");
        when(request.getParameter("order")).thenReturn("asc");

        servlet.doGet(request, response);

        verify(productDao).findProducts(
                "phone1", "price", "asc");
    }

    @Test
    void testDoGetNullFindProductsParameters() throws ServletException, IOException {
        when(request.getParameter("query")).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("order")).thenReturn(null);

        servlet.doGet(request, response);

        verify(productDao).findProducts(
                null, null, null);
    }
}