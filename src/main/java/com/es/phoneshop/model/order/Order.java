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

    public Order() {
    }

    public Order(Order original) {
        super(original);
        this.uuid = original.getUuid();
        this.deliveryCosts = original.getDeliveryCosts();
        this.orderTotalPrice = original.getOrderTotalPrice();
        this.firstName = original.getFirstName();
        this.lastName = original.getLastName();
        this.phone = original.getPhone();
        this.deliveryDate = original.getDeliveryDate();
        this.deliveryAddress = original.getDeliveryAddress();
        this.paymentMethod = original.getPaymentMethod();
    }
}

