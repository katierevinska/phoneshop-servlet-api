package com.es.phoneshop.model.cart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private final List<CartItem> items;
    public Cart() {
        items = new ArrayList<>();
    }
    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
