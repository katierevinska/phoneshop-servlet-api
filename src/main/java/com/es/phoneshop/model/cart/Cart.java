package com.es.phoneshop.model.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Getter
@Setter
public class Cart implements Serializable {
    protected List<CartItem> items = new ArrayList<>();
    protected int totalQuantity;
    protected BigDecimal cartPrice;
    protected Currency currency;

    @SneakyThrows
    public Cart clone() {
        Cart cart = (Cart) super.clone();
        cart.setTotalQuantity(this.totalQuantity);
        cart.setCartPrice(this.cartPrice);
        cart.setCurrency(this.currency);
        cart.setItems(this.items.stream().map(CartItem::clone).toList());
        return cart;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
