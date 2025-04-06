package com.es.phoneshop.services.order;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultOrderServiceTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private Cart cart;
    @Mock
    private CartItem cartItem;
    @Mock
    private Product product;

    private DefaultOrderService orderService;

    @BeforeEach
    public void setup() {
        orderService = new DefaultOrderService();
        orderService.setOrderDao(orderDao);
    }

    @Test
    void testPlaceOrder() {
        Order order = new Order();
        UUID orderId = UUID.randomUUID();
        when(orderDao.save(order)).thenReturn(orderId);

        UUID result = orderService.placeOrder(order);

        assertEquals(orderId, result);
        verify(orderDao).save(order);
    }

    @Test
    void testConstructOrderByCart() {
        when(cart.getItems()).thenReturn(List.of(cartItem));
        when(cart.getCurrency()).thenReturn(Currency.getInstance("USD"));
        when(cart.getCartPrice()).thenReturn(BigDecimal.valueOf(100));
        when(cartItem.getQuantity()).thenReturn(2);
        when(cartItem.getProduct()).thenReturn(product);

        Order order = orderService.constructOrderByCart(cart);

        assertEquals(1, order.getItems().size());
        assertEquals(Currency.getInstance("USD"), order.getCurrency());
        assertEquals(BigDecimal.valueOf(100), order.getCartPrice());
        assertEquals(BigDecimal.valueOf(10), order.getDeliveryCosts());
        assertEquals(BigDecimal.valueOf(110), order.getOrderTotalPrice());
    }

    @Test
    void testGetOrderWhenExists() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        when(orderDao.getOrder(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrder(orderId);

        assertTrue(result.isPresent());
        assertSame(order, result.get());
        verify(orderDao).getOrder(orderId);
    }

    @Test
    void testGetOrderWhenNotExists() {
        UUID orderId = UUID.randomUUID();
        when(orderDao.getOrder(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrder(orderId);

        assertFalse(result.isPresent());
        verify(orderDao).getOrder(orderId);
    }
}