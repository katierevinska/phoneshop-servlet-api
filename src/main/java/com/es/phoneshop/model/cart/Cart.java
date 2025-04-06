package com.es.phoneshop.model.cart;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class Cart implements Serializable {
    protected List<CartItem> items = new ArrayList<>();
    protected int totalQuantity;
    protected BigDecimal cartPrice;
    protected Currency currency;

    public Cart() {
    }

    public Cart(Cart original) {
        this.totalQuantity = original.totalQuantity;
        this.cartPrice = original.cartPrice;
        this.currency = original.currency;
        this.items = original.items.stream()
                .map(CartItem::new)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
