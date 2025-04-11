package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Order extends Cart {
    private UUID uuid;
    private BigDecimal deliveryCosts;
    private BigDecimal orderTotalPrice;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate deliveryDate;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;

    @Override
    public Order clone() {
        Order order = (Order) super.clone();
        order.setUuid(this.uuid);
        order.setDeliveryCosts(this.deliveryCosts);
        order.setOrderTotalPrice(this.orderTotalPrice);
        order.setFirstName(this.firstName);
        order.setLastName(this.lastName);
        order.setPhone(this.phone);
        order.setDeliveryDate(this.deliveryDate);
        order.setDeliveryAddress(this.deliveryAddress);
        order.setPaymentMethod(this.paymentMethod);
        return order;
    }
}

