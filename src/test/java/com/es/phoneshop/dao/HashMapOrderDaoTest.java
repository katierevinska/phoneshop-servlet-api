package com.es.phoneshop.dao;

import com.es.phoneshop.model.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HashMapOrderDaoTest {
    private HashMapOrderDao orderDao;

    @BeforeEach
    public void setup() {
        orderDao = HashMapOrderDao.getInstance();
    }

    @Test
    void testSaveOrder() {
        Order order = new Order();
        UUID orderId = orderDao.save(order);

        assertNotNull(orderId);
        assertEquals(orderId, order.getUuid());
        assertTrue(orderDao.getOrder(orderId).isPresent());
    }

    @Test
    void testGetOrderWhenExists() {
        Order order = new Order();
        UUID orderId = orderDao.save(order);

        Optional<Order> result = orderDao.getOrder(orderId);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    void testGetOrderWhenNotExists() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Order> result = orderDao.getOrder(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetOrderWithNullId() {
        Optional<Order> result = orderDao.getOrder(null);

        assertFalse(result.isPresent());
    }
}