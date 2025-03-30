package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.cart.DefaultCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
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
        processSessionAttributeFromDoPost(request, "errors");
        processSessionAttributeFromDoPost(request, "message");

        Cart cart = cartService.getCart(request.getSession());
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getPathInfo().substring(16);
        Long id = Long.parseLong(idStr);
        Cart cart = cartService.getCart(request.getSession());
        cartService.delete(cart, id);
        response.sendRedirect(request.getContextPath() + "/cart?message=product+was+deleted+from+cart");
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<UprateCartItemInfo> uprateCartItemsInfo = objectMapper.readValue(request.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UprateCartItemInfo.class));

        Cart cart = cartService.getCart(request.getSession());
        Map<Long, String> errorMessageByProductInCart = new HashMap<>();
        uprateCartItemsInfo.forEach(cartItem -> processCartItem(cart, cartItem, errorMessageByProductInCart));

        response.setContentType("application/json");
        if (errorMessageByProductInCart.isEmpty()) {
            request.getSession().setAttribute("message", "cart was updated");
        } else {
            request.getSession().setAttribute("errors", errorMessageByProductInCart);
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void processSessionAttributeFromDoPost(HttpServletRequest request, String attribute) {
        if (request.getSession().getAttribute(attribute) != null) {
            request.setAttribute(attribute, request.getSession().getAttribute(attribute));
            request.getSession().setAttribute(attribute, null);
        }
    }
    private void processCartItem(Cart cart, UprateCartItemInfo cartItem, Map<Long, String> errorMessageByProductInCart) {
        Long productId = Long.parseLong(cartItem.productId);
        try {
            int quantity = Integer.parseInt(cartItem.quantity);
            cartService.update(cart, productId, quantity);
        } catch (NumberFormatException e) {
            errorMessageByProductInCart.put(productId, cartItem.quantity + " not a number");
        } catch (OutOfStockException e) {
            errorMessageByProductInCart.put(productId, e.getMessage());
        }
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    private static class UprateCartItemInfo {
        public String productId;
        public String quantity;
    }
}

