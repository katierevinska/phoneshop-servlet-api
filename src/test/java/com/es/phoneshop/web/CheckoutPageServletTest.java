package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.order.OrderService;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private CartService cartService;
    @Mock
    private OrderService orderService;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cart cart;
    @Mock
    private Order order;

    private final CheckoutPageServlet servlet = new CheckoutPageServlet();

    @BeforeEach
    public void setup() throws ServletException {
        servlet.init();
        servlet.setCartService(cartService);
        servlet.setOrderService(orderService);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(orderService.constructOrderByCart(cart)).thenReturn(order);
        when(orderService.getPaymentMethods())
                .thenReturn(List.of(new PaymentMethod[]{PaymentMethod.CREDIT_CARD, PaymentMethod.CASH}));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("order", order);
        verify(request).setAttribute("paymentMethods", orderService.getPaymentMethods());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostSuccess() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(orderService.constructOrderByCart(cart)).thenReturn(order);
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("phone")).thenReturn("+1234567890");
        when(request.getParameter("deliveryAddress")).thenReturn("123 Main St");
        when(request.getParameter("paymentMethod")).thenReturn(PaymentMethod.CASH.name());
        when(request.getParameter("deliveryDate")).thenReturn(LocalDate.now().toString());
        when(orderService.placeOrder(order)).thenReturn(UUID.randomUUID());

        servlet.doPost(request, response);

        verify(cartService).clearCart(cart);
        verify(response).sendRedirect(contains("/order/overview/"));
    }

    @Test
    void testDoPostWithEmptyFields() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(orderService.constructOrderByCart(cart)).thenReturn(order);
        when(request.getParameter("firstName")).thenReturn("");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("phone")).thenReturn("+1234567890");
        when(request.getParameter("deliveryDate")).thenReturn(LocalDate.now().toString());
        when(request.getParameter("deliveryAddress")).thenReturn("123 Main St");
        when(request.getParameter("paymentMethod")).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        Map<String, String> errors = new HashMap<>();
        errors.put("firstName", "value can't be empty");
        errors.put("paymentMethod", "value can't be empty");
        verify(request).setAttribute("errors", errors);
        verify(request).setAttribute("paymentMethods", orderService.getPaymentMethods());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPostWithDeliveryDateInPast() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(orderService.constructOrderByCart(cart)).thenReturn(order);
        when(request.getParameter("firstName")).thenReturn("John");
        when(request.getParameter("lastName")).thenReturn("Doe");
        when(request.getParameter("phone")).thenReturn("+1234567890");
        when(request.getParameter("deliveryAddress")).thenReturn("123 Main St");
        when(request.getParameter("paymentMethod")).thenReturn(PaymentMethod.CASH.name());
        when(request.getParameter("deliveryDate")).thenReturn(
                LocalDate.of(2023, 12, 12).toString());

        servlet.doPost(request, response);

        Map<String, String> errors = new HashMap<>();
        errors.put("deliveryDate", "delivery date in the past");
        verify(request).setAttribute("errors", errors);
        verify(request).setAttribute("paymentMethods", orderService.getPaymentMethods());
        verify(requestDispatcher).forward(request, response);
    }
}