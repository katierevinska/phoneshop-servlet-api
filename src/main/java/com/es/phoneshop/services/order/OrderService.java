package com.es.phoneshop.services.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    UUID placeOrder(Order order);

    Order constructOrderByCart(Cart cart);

    Optional<Order> getOrder(UUID uuid);

    default List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }
}
