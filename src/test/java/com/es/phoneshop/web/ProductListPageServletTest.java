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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    @Mock
    private RecentViewProductsService recentViewProductsService;
    @Mock
    private OutOfStockException outOfStockException;
    @Mock
    private HttpSession httpSession;
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;
    @Mock
    private Product product;

    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @BeforeEach
    public void setup() throws ServletException {
        servlet.init();
        servlet.setProductDao(productDao);
        servlet.setRecentViewProductsService(recentViewProductsService);
        servlet.setCartService(cartService);
    }

    @Test
    void testDoGetProcess() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(recentViewProductsService.getRecentViewProducts(any())).thenReturn(new RecentViewProducts());
        when(request.getSession()).thenReturn(httpSession);
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());
        verify(request).setAttribute(eq("recentViewProducts"), any());
    }

    @Test
    void testDoGetNotNullFindProductsParameters() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        when(recentViewProductsService.getRecentViewProducts(any())).thenReturn(new RecentViewProducts());
        when(request.getParameter("query")).thenReturn("phone1");
        when(request.getParameter("sort")).thenReturn("price");
        when(request.getParameter("order")).thenReturn("asc");

        servlet.doGet(request, response);

        verify(productDao).findProducts(
                "phone1", "price", "asc");
    }

    @Test
    void testDoGetNullFindProductsParameters() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        when(recentViewProductsService.getRecentViewProducts(any())).thenReturn(new RecentViewProducts());
        when(request.getParameter("query")).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("order")).thenReturn(null);

        servlet.doGet(request, response);

        verify(productDao).findProducts(
                null, null, null);
    }

    @Test
    void testDoPostProductExistsAndValidQuantity() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);

        String json = "{\"productId\":\"1\",\"quantity\":\"2\"}";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));
        when(product.getId()).thenReturn(1L);
        when(product.getDescription()).thenReturn("description");

        servlet.doPost(request, response);

        verify(cartService).add(cart, 1L, 2);
        verify(httpSession).setAttribute("message", "2 description added to cart");
        verify(response).sendRedirect(request.getContextPath() + "/products");
    }

    @Test
    void testDoPostProductExistsAndInvalidQuantity() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(httpSession);
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        String json = "{\"productId\":\"1\",\"quantity\":\"invalid\"}";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));

        servlet.doPost(request, response);

        verify(cartService, never()).add(any(), anyLong(), anyInt());
        verify(httpSession).setAttribute(eq("error"), any());
        verify(httpSession).setAttribute(eq("productId"), any());
        verify(httpSession).setAttribute(eq("quantity"), any());
        verify(response).sendRedirect(request.getContextPath() + "/products");
    }

    @Test
    void testDoPostOutOfStock() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(httpSession);
        when(product.getId()).thenReturn(1L);
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));
        when(cartService.getCart(httpSession)).thenReturn(cart);

        String json = "{\"productId\":\"1\",\"quantity\":\"2\"}";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));
        doThrow(outOfStockException).when(cartService).add(cart, 1L, 2);

        servlet.doPost(request, response);

        verify(httpSession).setAttribute(eq("error"), any());
        verify(httpSession).setAttribute(eq("productId"), any());
        verify(httpSession).setAttribute(eq("quantity"), any());
        verify(response).sendRedirect(request.getContextPath() + "/products");
    }

    @Test
    void testDoPostProductNotFound() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(productDao.getProduct(1L)).thenReturn(Optional.empty());

        String json = "{\"productId\":\"1\",\"quantity\":\"2\"}";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));

        servlet.doPost(request, response);

        verify(request).setAttribute("message", "product not found");
        verify(request).setAttribute("notFoundId", 1L);
        verify(response).sendError(404);
        verify(request.getRequestDispatcher("/WEB-INF/pages/notFoundProduct.jsp")).forward(request, response);
    }
}