package com.es.phoneshop.services.cart;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import jakarta.servlet.http.HttpSession;

public interface CartService {
    Cart getCart(HttpSession httpSession);

    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;
}
