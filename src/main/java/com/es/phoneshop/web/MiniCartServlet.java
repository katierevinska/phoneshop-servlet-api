package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.cart.DefaultCartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MiniCartServlet extends HttpServlet {
    private CartService cartService;
    @Override
    public void init() throws ServletException {
        super.init();
        cartService = new DefaultCartService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/pages/minicart.jsp").include(request, response);
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
