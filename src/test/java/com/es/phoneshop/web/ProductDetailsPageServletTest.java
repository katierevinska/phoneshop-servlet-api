package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;

    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @BeforeEach
    public void setup() throws ServletException {
        servlet.init();
        servlet.setProductDao(productDao);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetNotExistsProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/29");
        when(productDao.getProduct(29L)).thenReturn(Optional.empty());

        servlet.doGet(request, response);
        verify(request).getRequestDispatcher("/WEB-INF/pages/notFoundProduct.jsp");

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetNotExistProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/122");
        when(productDao.getProduct(122L)).thenReturn(Optional.of(new Product()));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("product"), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/productDetails.jsp");
        verify(productDao).getProduct(122L);
    }
}