package com.es.phoneshop.services.order;

import com.es.phoneshop.dao.HashMapOrderDao;
import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class DefaultOrderService implements OrderService {
    private OrderDao orderDao = HashMapOrderDao.getInstance();

    @Override
    public UUID placeOrder(Order order) {
        return orderDao.save(order);
    }

    @Override
    public Order constructOrderByCart(Cart cart) {
        Order order = new Order();
        order.setItems(cart.getItems().stream()
                .map(CartItem::new)
                .toList());
        order.setCurrency(cart.getCurrency());
        BigDecimal deliveryCost = calculateDeliveryCost();
        order.setCartPrice(cart.getCartPrice());
        order.setDeliveryCosts(deliveryCost);
        order.setTotalQuantity(cart.getTotalQuantity());
        order.setOrderTotalPrice(cart.getCartPrice().add(deliveryCost));

        return order;
    }

    @Override
    public Optional<Order> getOrder(UUID uuid) {
        return orderDao.getOrder(uuid);
    }

    private BigDecimal calculateDeliveryCost() {
        return BigDecimal.TEN;
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
}
