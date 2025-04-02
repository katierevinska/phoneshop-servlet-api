package com.es.phoneshop.services.cart;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCartServiceTest {
    @Mock
    private HttpSession httpSession;
    @Mock
    private ProductDao productDao;
    @Mock
    private Cart cart;
    @Mock
    private Product product;
    private DefaultCartService cartService;

    @BeforeEach
    public void setup() {
        cartService = new DefaultCartService();
        cartService.setProductDao(productDao);
    }

    @Test
    void testGetCartWhenCartExists() {
        Cart cart = new Cart();
        when(httpSession.getAttribute(anyString())).thenReturn(cart);

        Cart result = cartService.getCart(httpSession);

        verify(httpSession).getAttribute(anyString());
        assertSame(cart, result);
    }

    @Test
    void testGetCartWhenCartDoesNotExist() {
        when(httpSession.getAttribute(anyString())).thenReturn(null);

        Cart result = cartService.getCart(httpSession);

        verify(httpSession).setAttribute(anyString(), any(Cart.class));
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testAddProductToCartWhenProductExists() throws OutOfStockException {
        when(product.getId()).thenReturn(1L);
        when(product.getStock()).thenReturn(10);

        CartItem cartItem = new CartItem(product, 2);
        when(cart.getItems()).thenReturn(List.of(cartItem));
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        cartService.add(cart, 1L, 3);

        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void testAddProductToCartWhenProductDoesNotExist() throws OutOfStockException {
        Cart cart = new Cart();
        when(productDao.getProduct(1L)).thenReturn(Optional.empty());

        cartService.add(cart, 1L, 3);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testAddProductToCartWhenOutOfStock() {
        when(product.getStock()).thenReturn(2);

        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            cartService.add(cart, 1L, 3);
        });
        assertEquals(2, exception.getAvailableStock());
        assertEquals(3, exception.getRequestedStock());
        assertEquals(product, exception.getProduct());
    }

    @Test
    void testAddExistingProductToCartWhenOutOfStock() {
        when(product.getStock()).thenReturn(5);
        when(product.getId()).thenReturn(1L);

        when(cart.getItems()).thenReturn(List.of(new CartItem(product, 3)));
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            cartService.add(cart, 1L, 3);
        });

        assertEquals(5, exception.getAvailableStock());
        assertEquals(6, exception.getRequestedStock());
        assertEquals(product, exception.getProduct());
    }

    @Test
    void testUpdateProductToCartWhenProductExists() throws OutOfStockException {
        when(product.getId()).thenReturn(1L);
        when(product.getStock()).thenReturn(10);

        CartItem cartItem = new CartItem(product, 2);
        when(cart.getItems()).thenReturn(List.of(cartItem));
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        cartService.update(cart, 1L, 3);

        assertEquals(3, cartItem.getQuantity());
    }

    @Test
    void testUpdateProductToCartWhenProductDoesNotExist() throws OutOfStockException {
        Cart cart = new Cart();
        when(productDao.getProduct(1L)).thenReturn(Optional.empty());

        cartService.update(cart, 1L, 3);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testUpdateProductToCartWhenOutOfStock() {
        when(product.getStock()).thenReturn(2);

        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            cartService.update(cart, 1L, 3);
        });
        assertEquals(2, exception.getAvailableStock());
        assertEquals(3, exception.getRequestedStock());
        assertEquals(product, exception.getProduct());
    }

    @Test
    void testUpdateExistingProductToCartWhenOutOfStock() {
        when(product.getStock()).thenReturn(2);
        when(product.getId()).thenReturn(1L);

        when(cart.getItems()).thenReturn(List.of(new CartItem(product, 2)));
        when(productDao.getProduct(1L)).thenReturn(Optional.of(product));

        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            cartService.update(cart, 1L, 3);
        });

        assertEquals(2, exception.getAvailableStock());
        assertEquals(3, exception.getRequestedStock());
        assertEquals(product, exception.getProduct());
    }

    @Test
    void testDeleteProductToCartWhenProductExists() {
        when(product.getId()).thenReturn(1L);
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 2));

        cartService.delete(cart, 1L);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testDeleteProductToCartWhenProductNotExists() {
        when(product.getId()).thenReturn(1L);
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(product, 2));

        cartService.delete(cart, 2L);

        assertEquals(1, cart.getItems().size());
    }
}