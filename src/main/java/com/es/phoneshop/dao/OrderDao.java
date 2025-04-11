package com.es.phoneshop.dao;

import com.es.phoneshop.model.order.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderDao {
    Optional<Order> getOrder(UUID uuid);
    UUID save(Order order);
}
