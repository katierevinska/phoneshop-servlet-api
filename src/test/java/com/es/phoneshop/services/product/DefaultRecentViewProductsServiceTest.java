package com.es.phoneshop.services.product;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentViewProducts;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRecentViewProductsServiceTest {
    @Mock
    private HttpSession httpSession;
    private DefaultRecentViewProductsService service;

    @BeforeEach
    public void setup() {
        service = new DefaultRecentViewProductsService();
    }

    @Test
    void testGetRecentViewProductsWhenAttributeExists() {
        RecentViewProducts recentViewProducts = new RecentViewProducts();
        when(httpSession.getAttribute(anyString()))
                .thenReturn(recentViewProducts);

        RecentViewProducts result = service.getRecentViewProducts(httpSession);

        verify(httpSession).getAttribute(anyString());
        assertSame(recentViewProducts, result);
    }

    @Test
    void testGetRecentViewProductsWhenAttributeDoesNotExist() {
        when(httpSession.getAttribute(anyString()))
                .thenReturn(null);

        RecentViewProducts result = service.getRecentViewProducts(httpSession);

        verify(httpSession).setAttribute(anyString(), any(RecentViewProducts.class));
        assertNotNull(result);
        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void testAddRecentViewProduct() {
        RecentViewProducts recentViewProducts = new RecentViewProducts();
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);

        service.updateRecentViewProducts(recentViewProducts, product);

        assertEquals(1, recentViewProducts.getProducts().size());
        assertEquals(product, recentViewProducts.getProducts().get(0));
    }

    @Test
    void testRecentViewProductExist() {
        RecentViewProducts recentViewProducts = new RecentViewProducts();
        recentViewProducts.setLimit(3);
        Product product3 = mock(Product.class);
        Product product2 = mock(Product.class);
        Product product1 = mock(Product.class);
        recentViewProducts.getProducts().add(product3);
        recentViewProducts.getProducts().add(product2);
        recentViewProducts.getProducts().add(product1);

        when(product1.getId()).thenReturn(1L);
        when(product2.getId()).thenReturn(2L);
        when(product3.getId()).thenReturn(3L);

        service.updateRecentViewProducts(recentViewProducts, product1);

        assertEquals(3, recentViewProducts.getProducts().size());
        assertEquals(product1, recentViewProducts.getProducts().get(0));
        assertEquals(product3, recentViewProducts.getProducts().get(1));
        assertEquals(product2, recentViewProducts.getProducts().get(2));

    }
}