package com.es.phoneshop.dao;

import com.es.phoneshop.model.order.Order;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapOrderDao implements OrderDao {
    private static final HashMapOrderDao instance = new HashMapOrderDao();
    private final ConcurrentHashMap<UUID, Order> orders = new ConcurrentHashMap<>();

    private HashMapOrderDao() {
    }

    public static HashMapOrderDao getInstance() {
        return instance;
    }

    @Override
    public Optional<Order> getOrder(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        Order order = orders.get(uuid);
        return Optional.ofNullable(order);
    }

    @Override
    public UUID save(Order order) {
        UUID uuid = UUID.randomUUID();
        order.setUuid(uuid);
        orders.put(uuid, order);
        return uuid;
    }
}
