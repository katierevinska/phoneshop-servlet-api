package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.exceptions.OutOfStockException;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartPageServletTest {
    private CartPageServlet cartPageServlet;

    @Mock
    private CartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private Cart cart;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OutOfStockException outOfStockException;

    @BeforeEach
    public void setup() throws ServletException {
        cartPageServlet = new CartPageServlet();
        cartPageServlet.init();
        cartPageServlet.setCartService(cartService);
    }

    @Test
    void testDoGetShouldSetCartAttributeAndForward() throws ServletException, IOException {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        cartPageServlet.doGet(request, response);

        verify(request).setAttribute("cart", cart);
        verify(request.getRequestDispatcher("/WEB-INF/pages/cart.jsp")).forward(request, response);
    }

    @Test
    void testDoPostShouldUpdateCartAndRedirect() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);

        String json = "[{\"productId\":1,\"quantity\":2}, {\"productId\":2,\"quantity\":3}]";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));

        cartPageServlet.doPost(request, response);

        verify(cartService).update(cart, 1L, 2);
        verify(cartService).update(cart, 2L, 3);
        verify(session).setAttribute(eq("message"), anyString());
        verify(response).sendRedirect(request.getContextPath() + "/cart");
    }

    @Test
    void testDoPostShouldHandleNumberFormatException() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);

        String json = "[{\"productId\":1,\"quantity\":\"invalid\"}]";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));

        cartPageServlet.doPost(request, response);

        verify(cartService, never()).update(any(), anyLong(), anyInt());

        Map<Long, String> expectedErrors = new HashMap<>();
        expectedErrors.put(1L, "invalid not a number");
        verify(session).setAttribute("errors", expectedErrors);
        verify(response).sendRedirect(request.getContextPath() + "/cart");
    }

    @Test
    void testDoPostShouldHandleOutOfStockException() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);

        String json = "[{\"productId\":1,\"quantity\":2}]";
        when(request.getInputStream()).thenReturn(new TestServletInputStream(json));
        doThrow(outOfStockException).when(cartService).update(cart, 1L, 2);

        cartPageServlet.doPost(request, response);

        verify(session).setAttribute(eq("errors"), any());
        verify(response).sendRedirect(request.getContextPath() + "/cart");
    }

    @Test
    void testDoDeleteShouldDeleteItemFromCartAndRedirect() throws ServletException, IOException {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        Long productId = 123L;
        when(request.getSession()).thenReturn(session);
        when(request.getPathInfo()).thenReturn("/deleteCartItem/" + productId);
        when(cartService.getCart(session)).thenReturn(cart);

        cartPageServlet.doDelete(request, response);

        verify(cartService).delete(cart, productId);
        verify(response).sendRedirect(request.getContextPath() + "/cart?message=product+was+deleted+from+cart");
    }

    @Test
    void testDoDeleteShouldHandleNumberFormatException() throws IOException {
        when(request.getPathInfo()).thenReturn("/deleteCartItem/invalid-id");

        assertThrows(NumberFormatException.class, () -> {
            cartPageServlet.doDelete(request, response);
        });

        verify(cartService, never()).delete(any(), anyLong());
        verify(response, never()).sendRedirect(anyString());
    }
}