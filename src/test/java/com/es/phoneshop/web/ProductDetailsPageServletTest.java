package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentViewProducts;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.product.RecentViewProductsService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    @Mock
    private CartService cartService;
    @Mock
    private RecentViewProductsService recentViewProductsService;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Product product;
    @Mock
    private Cart cart;

    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @BeforeEach
    public void setup() throws ServletException {
        servlet.init();
        servlet.setProductDao(productDao);
    }

    @Test
    void testDoGetNotExistsProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/29");
        when(productDao.getProduct(29L)).thenReturn(Optional.empty());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);
        verify(request).getRequestDispatcher("/WEB-INF/pages/notFoundProduct.jsp");

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetExistProduct() throws ServletException, IOException {
        servlet.setRecentViewProductsService(recentViewProductsService);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        when(request.getSession()).thenReturn(httpSession);
        when(recentViewProductsService.getRecentViewProducts(any())).thenReturn(new RecentViewProducts());
        doNothing().when(recentViewProductsService).updateRecentViewProducts(any(), any());

        when(request.getPathInfo()).thenReturn("/122");
        when(productDao.getProduct(122L)).thenReturn(Optional.of(product));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("product"), any());
        verify(request).setAttribute(eq("cart"), any());
        verify(recentViewProductsService).updateRecentViewProducts(any(), any());
        verify(request).getRequestDispatcher("/WEB-INF/pages/productDetails.jsp");
        verify(productDao).getProduct(122L);
    }

    @Test
    void testDoPostNotExistsProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/29");
        when(productDao.getProduct(29L)).thenReturn(Optional.empty());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/pages/notFoundProduct.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostQuantityNotANumber() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/122");
        when(product.getId()).thenReturn(122L);
        when(productDao.getProduct(122L)).thenReturn(Optional.of(product));
        when(request.getParameter("quantity")).thenReturn("invalid");
        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains(
                "/products/122?error=invalid+not+a+number&productQuantity=invalid"));
    }

    @Test
    void testDoPostOutOfStock() throws ServletException, IOException, OutOfStockException {
        when(product.getId()).thenReturn(122L);
        when(request.getPathInfo()).thenReturn("/122");
        when(productDao.getProduct(122L)).thenReturn(Optional.of(product));
        when(request.getParameter("quantity")).thenReturn("5");
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);

        doThrow(new OutOfStockException(product, 10, 5))
                .when(cartService).add(any(), anyLong(), anyInt());
        servlet.setCartService(cartService);

        servlet.doPost(request, response);
        verify(response).sendRedirect(contains(
                "/products/122?error=out of stock, need 10 but only 5 is available&productQuantity=5"));
    }

    @Test
    void testDoPostSuccess() throws ServletException, IOException, OutOfStockException {
        when(product.getDescription()).thenReturn("smt");
        when(product.getId()).thenReturn(122L);
        when(request.getPathInfo()).thenReturn("/122");
        when(productDao.getProduct(122L)).thenReturn(Optional.of(product));
        when(request.getParameter("quantity")).thenReturn("3");
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(httpSession);

        when(cartService.getCart(httpSession)).thenReturn(cart);
        servlet.setCartService(cartService);

        servlet.doPost(request, response);

        verify(cartService).add(cart, 122L, 3);
        verify(response).sendRedirect(contains("/products/122?message=3+smt+added+to+cart"));
    }
}