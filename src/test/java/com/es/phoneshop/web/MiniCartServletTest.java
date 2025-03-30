package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.cart.CartService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiniCartServletTest {
    private MiniCartServlet miniCartServlet;
    @Mock
    private CartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private Cart cart;

    @BeforeEach
    public void setup() throws ServletException {
        miniCartServlet = new MiniCartServlet();
        miniCartServlet.init();
        miniCartServlet.setCartService(cartService);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    void testDoGetShouldSetCartAttributeAndForward() throws ServletException, IOException {
        when(cartService.getCart(session)).thenReturn(cart);

        miniCartServlet.doGet(request, response);

        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).include(request, response);
    }
}